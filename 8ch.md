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