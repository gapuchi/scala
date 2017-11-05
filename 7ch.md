## For Comprehension in Depth

Reap

- for comprehension contains
  - 1+ generator expressions
  - optional guard for filtering
  - value definitions
- the output can be yielded to create a collection

Under the hood

- for is actual syntatical sugar of
  - foreach
  - map
  - flatMap
  - withFilter //withFilter is like filter except it doesn't produce intermediary outputs

Translation Rules of for Comprehensions

- generator `pat <- expr`
  - `pat` is a pattern expression
  - We convert the above expression into the following
  ```scala
  pat <- expr.withFilter {
    case pat => true;
    case _ => false;
  }
  ```
- other translation are applied repeatedly
  - 1 generator and yield `for( pat <- expr ) yield expr2`
  ```scala
  expr map { case pat => expr2 }
  ```
  - 1 generator and loop `for( pat <- expr ) expr2`
  ```scala
  expr foreach { case pat => expr2 }
  ```
  - 2+ generators `for ( pat1 <- expr1; pat2 <- expr2; ... ) yield exprN`
  ```scala
  expr1 flatMap { case pat1 => for (pat2 <- expr2 ...) yield exprN }    
  ```
  - 2+ generators with for loop `for ( pat1 <- expr1; pat2 <- expr2; ... ) exprN`
  ```scala
  expr1 foreach { case pat1 => for (pat2 <- expr2 ...) yield exprN }
  ```
  - generator with guard `pat1 <- expr1 if guard`
  ```scala
  pat1 <- expr1 withFilter ((arg1, arg2, ...) => guard)
  ```

## Options and Other Container Types

Any type that can be considered a container could support these methods and allow us to use instances of the type in for comprehensions. As long as it has the four methods:

- `foreach`
- `map`
- `flatMap`
- `withFilter`

### Option as a Container

`Option` is a binary container, either implemented as `Some` or `None`. Suppose we would like to filter out the `None` results.

```scala
val results: Seq[Option[Int]] = Vector(Some(10), None, Some(20))
val results2 = for {
  Some(i) <- results
} yield (2 * i)
```

This translated results

```scala
val results2b = for {
  Some(i) <- results withFilter {
    case Some(i) => true
    case None => false
  }
} yield (2 * i)

val results2c = results withFilter {
  case Some(i) => true
  case None => false
} map {
  case Some(i) -> (2 * i)
}
```

### Either extends Option

`Option` handles either 0 or 1 thing while `Either` handles 1 or another thing.

```scala
Either[+A,+B]
```

Either is an abstract class that has two subclasses that are defined. `Left[A]` and `Right[B]`. By convention left `Left` is used for exception and error handling and `Right` is used for the intended object.

```scala
val x: Either[String, Int] = Left("boo")
val y: Either[String, Int] = Right(12)

//Using the unapply method
val z: String Either Int = Left("boo")
```

As for the container methods, it cannot be applied to an Either directly since it may have two different values and would need a different formula for each side. So you would need to the get the `x.left` or `x.right`. The left and right create projection that have these methods

```scala
scala> x.left
res0: scala.util.Either.LeftProjection[String,Int] = LeftProjection(Left(boo))

scala> x.right
res1: scala.util.Either.RightProjection[String,Int] = RightProjection(Left(boo))

scala> y.left
res2: scala.util.Either.LeftProjection[String,Int] =  LeftProjection(Right(12))

scala> y.right
res3: scala.util.Either.RightProjection[String,Int] = RightProjection(Right(12))
```

The `LeftProjection` and `RightProjection` can either hold a `Left` or `Right` instance.

```scala
scala> l.left.map(_.size)
res4: Either[Int,Int] = Left(3)

scala> r.left.map(_.size)
res5: Either[Int,Int] = Right(12)

scala> l.right.map(_.toDouble)
res6: Either[String,Double] = Left(boo)

scala> r.right.map(_.toDouble)
res7: Either[String,Double] = Right(12.0)
```

When you `map` a `LeftProjection` and it holds a `Left`, it works like `Option`. However when you `map` a `LeftProjection` and it holds a `Right` object, it doesn't affect the object and returns it unchanged.

The map function takes in a `String` and outputs a `Int` so now the `Either` object is `Either[Int,Int]`

#### Throwing Exceptions vs Either values

Read the section but basically it lets the program to keep control and you can determine programmatically how to proceed.

### Try is Either with Left holding a Throwable

```scala
sealed abstract class Try[+T] extends AnyRef {...}
final case class Success[+T](value: T) extends Try[T] {...}
final case class Failure[+T](exception: Throwable) extends Try[T] {...}
```

### Scalaz Validation

TBR