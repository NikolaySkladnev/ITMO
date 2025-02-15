del(N, Divisor) :- 0 is N mod Divisor, !.
del(N, Divisor) :- Divisor * Divisor < N, NextDivisor is Divisor + 1, del(N, NextDivisor).

prime(2).
prime(N) :- \+ del(N, 2).

composite(N) :- \+ prime(N).

find_divisors(1, Divisors, CheckDivisors, _) :- reverse(CheckDivisors, Divisors).

find_divisors(N, Divisors, CheckDivisors, Divisor) :-
    prime(Divisor),
    0 is N mod Divisor,
    N1 is N // Divisor,
    find_divisors(N1, Divisors, [Divisor | CheckDivisors], Divisor).

find_divisors(N, Divisors, CheckDivisors, Divisor) :-
    Divisor =< N,
    NextDivisor is Divisor + 1,
    find_divisors(N, Divisors, CheckDivisors, NextDivisor).

prime_divisors(N, Divisors) :-
    number(N),
    find_divisors(N, Divisors, [], 2), !.

nth_prime(Count, Prime) :-
	check_number(Count, Prime, 1, 2), !.

check_number(Count, Prime, Count, Prime) :- prime(Prime), !.

check_number(Count, Prime, CheckCount, CheckPrime) :-
    prime(CheckPrime),
    NewCount is CheckCount + 1,
    NewPrime is CheckPrime + 1,
    check_number(Count, Prime, NewCount, NewPrime).

check_number(Count, Prime, CheckCount, CheckPrime) :-
    NewPrime is CheckPrime + 1,
    check_number(Count, Prime, CheckCount, NewPrime).
