map_build([], TreeMap, TreeMap).

map_build([Element | RestMap], TempMap, TreeMap) :-
    map_put(Element, TempMap, NewTempMap),
    map_build(RestMap, NewTempMap, TreeMap).

map_build(List, TreeMap) :-
    map_build(List, null, TreeMap).

map_put((Key, Value), null, node((Key, Value), null, null)).

map_put((Key, NewValue), node((Key, _), LeftParent, RightParent), node((Key, NewValue), LeftParent, RightParent)).

map_put((Key, Value), node((KeyParent, ValueParent), LeftParent, null), node((KeyParent, ValueParent), NewLeftParent, null)) :-
    KeyParent < Key,
    map_put((Key, Value), LeftParent, NewLeftParent).

map_put((Key, Value), node((KeyParent, ValueParent), null, RightParent), node((KeyParent, ValueParent), null, NewRightParent)) :-
    KeyParent > Key,
    map_put((Key, Value), RightParent, NewRightParent).

map_get(node((Key, Value), Left, Rigth), Key, Value) :- !.
map_get(node((Key, Value), Left, Rigth), CheckKey, CehckValue) :-
    Key > CheckKey,
    map_get(Rigth, CheckKey, CehckValue).

map_get(node((Key, Value), Left, Rigth), CheckKey, CehckValue) :-
    Key < CheckKey,
    map_get(Left, CheckKey, CehckValue).

map_lastKey(node((Key, Value), null, null), Key).

map_lastKey(node((Key, Value), LeftParent, RightParent), CheckKey) :-
    map_lastKey(LeftParent, CheckKey).

map_lastValue(node((Key, Value), null, null), Value).
map_lastValue(node((Key, Value), LeftParent, RightParent), CheckValue) :-
    map_lastValue(LeftParent, CheckValue).

map_lastEntry(node((Key, Value), null, null), (Key, Value)).
map_lastEntry(node((Key, Value), LeftParent, RightParent), (CheckKey, CheckValue)) :-
    map_lastEntry(LeftParent, (CheckKey, CheckValue)).
