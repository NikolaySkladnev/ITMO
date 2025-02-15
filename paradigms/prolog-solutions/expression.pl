lookup(Name, [(Name, Value) | _], Value) :- !.
lookup(Name, [_ | Tail], Value) :- lookup(Name, Tail, Value).

operation(op_add, A, B, R) :- R is A + B.
operation(op_subtract, A, B, R) :- R is A - B.
operation(op_multiply, A, B, R) :- R is A * B.
operation(op_divide, A, B, R) :- R is A / B.
operation(op_negate, A, R) :- R is -1 * A.
operation(op_square, A, R) :- R is A * A.
operation(op_sqrt, A, R) :- R is sqrt(A).

evaluate(const(Value), _, Value).
evaluate(variable(Name), Vars, R) :- lookup(Name, Vars, R).
evaluate(operation(Op, A), Vars, R) :-
    evaluate(A, Vars, AV),
    operation(Op, AV, R), !.
evaluate(operation(Op, A, B), Vars, R) :-
    evaluate(A, Vars, AV),
    evaluate(B, Vars, BV),
    operation(Op, AV, BV, R), !.

:- load_library('alice.tuprolog.lib.DCGLibrary').

whitespace --> [].
whitespace --> [' '], whitespace.

nonvar(V, T) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

number_p([]) --> [].
number_p([H | T]) --> { H = '-', T = [_ | _]}, [H], number_p(T).
number_p([H | T]) --> { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.']) }, [H], number_p(T).

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_negate) --> ['n', 'e', 'g', 'a', 't', 'e'].
op_p(op_square) --> ['s', 'q', 'u', 'a', 'r', 'e'].
op_p(op_sqrt) --> ['s', 'q', 'r', 't'].

expr_p(const(Value)) -->
  whitespace,
  { nonvar(Value, number_chars(Value, Chars)) },
  whitespace,
  number_p(Chars),
  whitespace,
  { Chars = [_ | _], number_chars(Value, Chars)},
  whitespace.

expr_p(variable(Name)) -->
    whitespace,
    [Name],
    whitespace,
    { member(Name, ['x', 'y', 'z']) }.

expr_p(operation(Op, A, B)) -->
    whitespace,
    ['('],
    whitespace,
    op_p(Op),
    [' '], whitespace,
    expr_p(A),
    [' '], whitespace,
    expr_p(B),
    whitespace,
    [')'],
    whitespace.

expr_p(operation(Op, A)) -->
    whitespace,
    ['('],
    whitespace,
    op_p(Op),
    [' '], whitespace,
    expr_p(A),
    whitespace,
    [')'],
    whitespace.

expr_str(E, A) :- ground(E), phrase(expr_p(E), C), atom_chars(A, C).
expr_str(E, A) :- atom(A), atom_chars(A, C), phrase(expr_p(E), C).

prefix_str(Expression, Atom) :- expr_str(Expression, Atom), !.