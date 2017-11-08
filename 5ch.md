# IMPLICITS

Implicits are nonlocal in the source code. These values and methods are imported into the local scope (except those that are automatically done thru `Predef`)

They are difficult because they are initially not obvious when looking at the source code.

## Implicit Arguments

When method arguments are prefixed by `implicit`, a type compatible value will be used from the enclosing scope. If not available, a compiler error occurs.

```scala
def calcTax(amount: Float)(implicit rate: Float): Float = amount * rate
```

Let's say the code that uses this method

```scala
implicit val currentTaxRate = 0.08F

...

val tax = calcTax(50000F)
```

For a function be implicit, it should have no arguments. Or all arguments shoud be implicit

```scala
def calcTax(amount: Float)(implicit rate: Float): Float = amount * rate

object SimpleStateSalesTax {
    implicit val rate: Float = 0.05F
}

case class ComplicatedSalesTaxData(
    baseRate: Float,
    isTaxHoliday: Boolean,
    storeId: Int)

object ComplicatedSalesTax {
    private def extraTaxRateForStore(id: Int): Float = { 0.0F }

    implicit def rate(implicit cstd: ComplicatedSalesTaxData): Float =
        if (cstd.isTaxHoliday) 0.0F
        else cstd.baseRate + extraTaxRateForStore(cstd.storeId)
}

{
    import SimpleStateSalesTax.rate

    val amount = 100F
    println(s"Tax on $amount = ${calcTax(amount)}")
}

{
    import ComplicatedSalesTax.rate
    implicit val myStore = ComplicatedSalesTaxData(0.06F, false, 1010)

    val amount = 100F
    println(s"Tax on $amount = ${calcTax(amount)}")
}
```

### Using `implicitly`

`Predef` defines a method called `implicitly`. It provides a shorthand way of defining method signatures that take a single implicit argument, where that argument is a parameterized type.

```scala
import math.Ordering

case class MyList[A](list: List[A]) {
    def sortBy1[B](f: A => B)(implicit ord: Ordering[B]): List[A] =
        list.sortBy(f)(ord)
    def sortBy2[B : Ordering](f: A => B): List[A] =
        list.sortBy(f)(implicitly[Ordering[B]])
}

val list = MyList(List(1,3,5,2,4))

list sortBy1 (i => -i)
list sortBy2 (i => -i)
```

`List.sortBy` takes a function that transforms the arguments into something that satifies `math.Ordering` (like Java's `Comparable`). A implicit argument is required that knows how to order instances of type `B`. (`Ordering[B]` for example has a method called `compare` that will return an integer whose sign will signify how they compare. `Ordering` is a [**trait**](http://www.scala-lang.org/api/2.12.3/scala/math/Ordering.html))

The first implementation is the familiar implentation. It takes in an argument that converts the items into items that can be compared and an implicit class that implements `Ordering[B]` will provide the methods to compare those items with each other. **In order for sortBy1 to be used, there must an instance in the scope that knows how to order `B`. B is bound by a *context* (which in this case is the ability to order instances)**.

The second implementation uses the shorthand Scala provides. The type parameter `B : Ordering` is called a *context bound*. **It implies a second, implicit argument list that takes an `Ordering[B]` instance**. Since this second argument list is not explicitly defined, Scala provides `Predef.implicitly` to reference this.

## Scenarios for Implicit Arguments

The main two benefits of using implicts are:

1. boilerplate elimination
2. constraints that reduce bugs or limit the allowed types that be wused with methods with parameterized types

TBR

## Implicit Conversions

Recall that there are several ways to create a tuple pair:

* `(1, "one")`
* `1 -> "one"`
* `1 → "one"`
* `Tuple2(1, "one")`
* `Pair(1, "one")`

Kinda wasteful to have multiple ways to createa tuple. The arrow function is popular when creating a `Map`.

```scala
Map("one" -> 1, "two" -> 2)
```

The `Map.apply` method expects a list of pairs:

```scala
def aplpy[A,B](elems: (A,B)*): Map[A.B]
```

Scala doesn't have anything for `->` so it is not wasteful. Rather `->` is a method and an *implicit conversion*. The trick is to use a "wrapper" that has `->` defined. `Predef` has one (methodphrased):

```scala
implicit final class ArrowAssoc[A](val self: A) {
    def -> [B](y: B): Tuple2[A, B] = Tuple2(self, y)
}
```

That looks worse. We'd need to something like `Map(new ArrowAssoc("one") -> 1)`, which is way more than `Map(("one", 1))`.

`Implicit` FTW. Since `ArrowAssoc` is an implicit class, here is what the compiler does:

1. See that we're trying to call the method `->` on a `String`.
2. Realizes there is no such method, so it looks for an *implicit conversion* in scope to a type that has that method.
3. Finds `ArrowAssoc`
4. Constructs an `ArrowAssoc`, passing the `"one"` string.
5. Resolves the `-> 1` portion and confirms that it satisfies the `Map.apply` method.

For something to be considered an *implicit conversion*, it must be declared with `implicit` and mush be either a class with one argument or a method with one argument.

Rules for compiler lookup and conversions:

1. No conversion will be attempted if the object and method combination type check successfully.
2. Only classes and methods with the implicit keyword are considered.
3. Only implicit classes and methods in the current scope are considered, as well as implicit methods defined in the companion object of the target type (see the following discussion).
4. Implicit methods aren’t chained to get from the available type, through intermediate types, to the target type. Only a method that takes a single available type instance and returns a target type instance will be considered.
5. No conversion is attempted if more than one possible conversion method could be applied. There must be one and only one, unambiguous possibility.

## Type Class Pattern

Type classes help avoid "kitchen-sink" abstractions (think of Java's `Object` that contains all, although not always useful, methods. Like `toString`). In Scala, we can add behavior on an ad hoc basis. **We aren't changing the types**. We are using implicit mechanism to wrap objects with types that have the behavior we want.

`Object.toString` isn't always useful, sometimes just printing the address of an object in the JVM heap. Scala provides a better use case. There are times for example we would want JSON to be human readable. We could use implicit conversions to add `toJSON` methods to any type.

Introducing the *Type Class Pattern*

`toJSON` example

```scala
case class Address(street: String, city: String)
case class Person(name: String, address: Address)

trait ToJSON {
    def toJSON(level: Int = 0): String

    val INDENTATION = " "
    def indentation(level: Int = 0): (String,String) =
        (INDENTATION * level, INDENTATION * (level+1))
}

implicit class AddressToJSON(address: Address) extends ToJSON {
    def toJSON(level: Int = 0): String = {
        val (outdent, indent) = indentation(level)
        s"""{
        |${indent}"street": "${address.street}",
        |${indent}"city": "${address.city}"
        |$outdent}""".stripMargin
    }
}

implicit class PersonToJSON(person: Person) extends ToJSON {
    def toJSON(level: Int = 0): String = {
        val (outdent, indent) = indentation(level)
        s"""{
        |${indent}"name": "${person.name}",
        |${indent}"address": ${person.address.toJSON(level + 1)}
        |$outdent}""".stripMargin
    }
}
val a = Address("1 Scala Lane", "Anytown")
val p = Person("Buck Trends", a)

println(a.toJSON())
println()
println(p.toJSON())
```

`toJSON` added to an existing classes. So it can be considered to be *extension methods*.

`toJSON` is not fixed to one class. So it can be considered to be *ad hoc polymorphism*.

## Technical Issues with Implicits

TBR

## Implicit Resolution Rules

TBR

## TBR