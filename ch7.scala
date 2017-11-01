For Comprehension in Depth

Recap
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
- generator
    - in 