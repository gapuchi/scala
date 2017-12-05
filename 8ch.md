# Object-Oriented Programming in Scala

## Class and Object Basics

* `class` - classes declaration
* `object`- singleton object declaration
* `final` - declaration prefix to prevent derived classes
* `abstract` - declaration prefix to prevent class instantiation
* `this` - an reference to itself ***not usually used in Scala**

Constructor boiler plate is absent in Scala.

Java:

```java
public class JPerson {
private String name;
private int age;
public JPerson(String name, int age) {
    this.name = name;
    this.age = age;
}

public void setName(String name) { this.name = name; }
public String getName() { return this.name; }

public void setAge(int age) { this.age = age; }
public int getAge() { return this.age; }
}
```

Scala:

```scala
class Person(var name: String, var age: Int)
```

Prefixing a constructor arugment with `var` makes it mutable and `val` makes it immutable.

Adding `case` in front of `class` assumes all arguments are `val`

```scala
case class Person(name: String, age: Int)
```

`method` is a function tied to an instance. Its definition starts with a `def`. Overloading methods are also present.

Member refers to a field, method, or type in the general sense.

A field and a method can have the same name if the method has an argument list.

Limitation of JVM is type erasures.

There is no `static` in Scala. Rather, an object is used to hold static members

If an `object` and `class` have the same name and are defined the in the same class, they are called `companions`.

* `case` classes have the companion object auto generated.

## Reference Versus Value Types

Primitive data types are stored on the stack or CPU

* `short`
* `int`
* `long`
* `float`
* `double`
* `boolean`
* `char`
* `byte`
* `void`

All other data types are refence types, stored on the heap.

All reference types are subtypes of `AnyRef`, which is a subtype of `Any`. All value types are subtypes of `AnyVal`, which is a subtype of `Any`. These are the only two subtypes of `Any`. (Java's `Object` is more like `AnyRef` not `Any`)

Scala follows Java for literal values, such as `val name = "blah"` = `val name = new String("blah")`. On top of Java literals, Scala has literals for tuples.

It's common for instances of reference types to be created using objects with `apply` methods which function as **factories**.

The Value types for Scala:

* `Short`
* `Int`
* `Long`
* `Float`
* `Double`
* `Boolean`
* `Char`
* `Byte`
* `Unit`

Instances of value types aren't stored on the heap, rather stored on the register or stack. They are always created with literal values. The literal for `Unit` is `()`, but that is rarely used. They actually don't have public constructors.

## Value Classes

Recall Scala has wrapper types to implement *type classes*. This however turns them into a reference types, removing the benefits of using a primitive type.

Scala 2.10 introduces *value classes* and a feature called *universal traits*. They limit what can be declared, but don't result in heap allocation for the wrappers.

To be a valid *value class*, it must:

* Have one and only one `val` argument
* The argument must not be a value class
* If the value class is parameterized, the `@specialized` annotation can't be used
* Doesn't define a secondary constructor
* Defines only methods, no other `val`'s or `var`'s.
* Can't override `equals` or `hashCode`.
* Defines no nested `trait`s, `class`es, or `object`s
* Cannot be subclassed
* Can only inherit from *universal traits*
* Must be a top level type or a member of an object that can be referenced.

```scala
class Dollar(val value: Float) extends AnyVal {
    override def toString = "$%.2f".format(value)
}

val benjamin = new Dollar(100)
```

```scala
class USPhoneNumber(val s: String) extends AnyVal {
    override def toString = {
        val digs = digits(s)
        val areaCode = digs.substring(0,3)
        val exchange = digs.substring(3,6)
        val subnumber = digs.substring(6,10)
        s"($areaCode) $exchange-$subnumber"
    }

    private def digits(str: String): String = str.replaceAll("""\D""", "")
}

val number = new USPhoneNumber("987-654-3210")
// Result: number: USPhoneNumber = (987) 654-3210
```

A *universal trait* has the following properties:

* It derives from `Any` but not other universal traits
* It defines only methods
* It does no initialization of its own

Here is USPhoneNumber with some universal traits:

```scala
trait Digitizer extends Any {
    def digits(s: String): String = s.replaceAll("""\D""", "")
}

trait Formatter extends Any {
    def format(areaCode: String, exchange: String, subnumber: String): String = 
        s"($areaCode) $exchange-$subnumber"
}

class USPhoneNumber(val s: String) extends AnyVal with Digitizer with Formatter {
    override def toString = {
        val digs = digits(s)
        val areaCode = digs.substring(0,3)
        val exchange = digs.substring(3,6)
        val subnumber = digs.substring(6,10)
        format(areaCode, exchange, subnumber)
    }
}

val number = new USPhoneNumber("987-654-3210")
// Result: number: USPhoneNumber = (987) 654-3210
```

`Formatter` is a solution to a design problem. There are multiple ways we would like to format a number, but since `USPhoneNumber` can only take in one argument, we can't specify as an argument. So we can *mixin* `trait`s.

There are cases where *universal traits* do require heap allocation:

* When a value class is passed to a function expecting a universal trait that the value class implements.
* A value calss instance is assigned to an `Array`.
* The type of a value class is used as a type parameter.

***Value type* refers to the `Short`, `Int`,`Double` etc. *Value class* refers to the new construct for custom classes derived from `AnyVal`**

## Parent Types

Like Java, Scala supports single inheritance. All classes must have one and only one parent class (except `Any` which has none). If we don't `extend` a parent, the default is `AnyRef`. You can `extend` *traits*, and *traits* can `extend` classes!

## Constructors in Scala

Scala has *primary constructors* and *auxilary/secondary constructors*. *Primary constructors* is the entire body of the class. Any parameters the constructors need are listed after the class name.

```scala
class MyClass(x: String, y: Int, z:Double) {
    ...
}
```

Auxilary constructors is named `this` and must call a primary or another constructor as its first line. The constructor being called must be defined prior. **Ordering matters**

```scala
case class Person(name: String, age: Option[Int], address: Option[Address]) {
    def this(name: String) = this(name, None, None)

    def this(name: String, age: Int) = this(name, Some(age), None)

    def this(name: String, age: Int, address: Address) =
        this(name, Some(age), Some(address))

    def this(name: String, address: Address) = this(name, None, Some(address))
}
```

There is a lot of re used logic for all the overloading constructors. Let's reduce by adding default values.

```scala
case class Person2(name: String, age: Option[Int] = None, address: Option[Address] = None)
```

Excellent. Let's say we want to make it even easier by removing the necessity to use the `new` keyword. Right now it will only work if we use the primary constructor. We would need to provide our own apply methods.

```scala
case class Person3(name: String, age: Option[Int] = None, address: Option[Address] = None)

object Person3 {
    // Because we are overloading a normal method (as opposed to constructors),
    // we must specify the return type annotation, Person3 in this case.
    def apply(name: String): Person3 = new Person3(name)

    def apply(name: String, age: Int): Person3 = new Person3(name, Some(age))

    def apply(name: String, age: Int, address: Address): Person3 =
        new Person3(name, Some(age), Some(address))

    def apply(name: String, address: Address): Person3 =
        new Person3(name, address = Some(address))
}
```

Solid

## Fields in Classes

Primary constructor arguments become instance fields if prefixed with `val` or `var`. For case classes, `val` is implied. For noncase classes, if we omit `val` or `var`, it doesn't become a field. They can still be referenced though in the class itself, doesn't matter if they do or don't have `val` or `var`. Use `val` or `var` if you want it to be externally visible.

Scala generates the code for getters and setter automatically.

```scala
class Name(var value: String)
```

Is basically:

```scala
class Name(s: String) {
    private var _value: String = s

    def value: String = _value

    def value_=(newValue: String): Unit = _value = newValue
}
```

Note the method `value_=`. When the compiler sees this, it allows the `_` to be dropped. So we can write `object.value = "Tom"` without the compiler yelling at us and makes it seem like we're directly changing the field.

### The Uniform Access Principle

Scala doesn't follow the convention of JavaBeans. It doesn't create methods such as `getValue` or `setValue`. Rather getting and setting would be both accessed as `value`. It isn't accessing the field directly, it is calling a method with the name `value`. The get and set is identical, calling `value`. This makes it convenient.

### Unary Methods

So the `_` can be dropped to make functions look nice, but what about **unary operations** like negate? For example if I have a number `c`, I'd like to call `-c`. Here's how:

```scala
case class Complex(real: Double, imag: Double) {
    def unary_- : Complex = Complex(-real, imag)
    def -(other: Complex) = Complex(real - other.real, imag - other.imag)
}

val c1 = Complex(1.1, 2.2)
val c2 = -c1                        // Complex(-1.1, 2.2)
val c3 = c1.unary_-                 // Complex(-1.1, 2.2)
val c4 = c1 - Complex(0.5, 1.0)     // Complex(0.6, 1.2)
```

The method is `unary_X` where `X` is the operator we want to use.

## Validating Input

`Predef` provides a set of overloaded methods called `require`. Also `ensuring` and `assume` methods exist.

## Calling Parent Class Constructors

A child class must invoke one of the parent constructors.

```scala
case class Person(
    name: String,
    age: Option[Int] = None,
    address: Option[Address] = None)

class Employee(
    name: String,
    age: Option[Int] = None,
    address: Option[Address] = None,
    val title: String = "[unknown]",
    val manager: Option[Employee] = None) extends Person(name, age, address) { ... }
```

We don't need to call super because the `extends` tells how it is being called.

### Good OO Design

The code above sucks. Employee has a mix of `val` and no keywords, but the root problem lies even deeper. You can extend a `class` from a `case class` or vice versa, but you **can't have a `case class` extend a `case class`**. This is because subclassing gets funky with autogenerated methods `toString`, `equals`, and `hashCode`.

Should an `Employee` instance be the same as a `Person` if they have the same `name`, `age`, and `address`? Equality should be associative, so if we say an `Employee` is the same as a `Person`, we should be able to say that the `Person` is equal to the `Employee` which makes no sense.

Actually, with `equals`, `Employee` doesn't override the `Person` methods. So it is treated as a `Person`.

The problem is that we're trying to *subclass state* which isn't what inheritance is designed for. **Traits make composition easier than Java's interfaces**

Good practice for inheritance:

* An abstract base class or trait is subclassed one level by concrete classes, including case classes.
* Concrete classes are never subclassed, except for two cases:
    * Classes that mix in other behaviors defined in traits. Ideally, those behaviors should be orthogonal, i.e., not overlapping.
    * Test-only versions to promote automated unit testing.
* When subclassing seems like the right approach, consider partitioning behaviors into traits and mix in those traits instead.
* Never split logical state across parent-child type boundaries.

How to separate concepts of `Person` and `Employee`?

```scala
case class Address(street: String, city: String, state: String, zip: String)

object Address {
def apply(zip: String) =
    new Address("[unknown]", Address.zipToCity(zip), Address.zipToState(zip), zip)

def zipToCity(zip: String) = "Anytown"
def zipToState(zip: String) = "CA"
}

trait PersonState {
    val name: String
    val age: Option[Int]
    val address: Option[Address]
}

case class Person(
    name: String,
    age: Option[Int] = None,
    address: Option[Address] = None) extends PersonState

trait EmployeeState {
    val title: String
    val manager: Option[Employee]
}

case class Employee(
    name: String,
    age: Option[Int] = None,
    address: Option[Address] = None,
    title: String = "[unknown]",
    manager: Option[Employee] = None) extends PersonState with EmployeeState
```

`Employee` is not a subclass of `Person` anymore, it mixes in `PersonState` trait.

## Nested Types

Scala let's us nest declarations and definitions

```scala
object Database {
    case class ResultSet(/*...*/)
    case class Connection(/*...*/)

    case class DatabaseException(message: String, cause: Throwable) extends RuntimeException(message, cause)

    sealed trait Status
    case object Disconnected extends Status
    case class Connected(connection: Connection) extends Status
    case class QuerySucceeded(results: ResultSet) extends Status
    case class QueryFailed(e: DatabaseException) extends Status
}

class Database {
    import Database._

    def connect(server: String): Status = ???
    def disconnect(): Status = ???

    def query(/*...*/): Status = ???
}
```

`???` is a method defined in `Predef`. It throws an exception.
