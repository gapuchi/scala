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

Any type that can be considered a container could support these methods and allow us to use instances of the type in for comprehensions