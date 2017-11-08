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

Instances of value types aren't stored on the heap, rather stored on the register or stack. Ther are always created with literal values. The literal for `Unit` is `()`, but that is rarely used. They actually don't have public constructors.

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