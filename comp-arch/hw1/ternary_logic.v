module xnor_gate(a,b,out);
  input wire a,b;
  output wire out;
  
  wire and_gate1_out;
  wire and_gate2_out;
  
  wire not_a;
  wire not_b;
  
  and_gate and_gate1(a,b,and_gate1_out);
  
  not_gate not_gate1(a, not_a);
  not_gate not_gate2(b, not_b);
  
  and_gate and_gate2(not_a,not_b,and_gate2_out);

  or_gate or_gate1(and_gate1_out, and_gate2_out, out);
  
endmodule


module and_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(a, b, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module not_gate(a,out);
  input wire a;
  output wire out;
  
  supply1 pwr;
  supply0 gnd;
  
  // 1 - куда уйдёт, 2 - что пришло, 3 - запитка
  pmos pmos1(out, pwr, a);
  nmos nmos1(out, gnd, a);

  
endmodule

module nand_gate(a, b, out);
  input wire a, b;
  output out;

  supply1 pwr;
  supply0 gnd;

  wire nmos1_out;

  pmos pmos1(out, pwr, a);
  pmos pmos2(out, pwr, b);

  nmos nmos1(nmos1_out, gnd, b);
  nmos nmos2(out, nmos1_out, a);
  // pmos pmos1(out, pwr, a);
  // nmos nmos1(out, gnd, a);
endmodule

module or_gate(a,b,out);
  input wire a,b;
  output wire out;
  wire pre_out;
  
  nor_gate nor_gate1(a,b,pre_out);
  not_gate not_gate1(pre_out, out);
  
  
endmodule
  
module nor_gate(a, b, out);
  
  
  supply1 pwr;
  supply0 gnd;
  
  input wire a;
  input wire b;
  
  wire pmos1_out;
  
  output wire out;
  
  // pmos с кружком когда 0 -- проводит ток
  // nmos без кружка когда 1 -- проводит ток
  
  // 1 - куда уйдёт, 2 - что пришло, 3 - запитка
  pmos pmos1(pmos1_out, pwr, a);
  pmos pmos2(out, pmos1_out, b);
  
  nmos nmos1(out, gnd, a);
  nmos nmos2(out, gnd, b);
endmodule

module ternary_min(a, b, out);
  input [1:0] a;
  input [1:0] b;
  
  supply1 pwr;
  supply0 gnd;

  initial begin
    // Системная функция $display выводит текст на экран во время симуляции.
    $display("Hello world");
    $dumpfile("dump.vcd");
    $dumpvars;
  end
  
  and_gate and_gate1(a[1], b[1], out[1]);
  
  wire xnor1;
  wire eval_and1;

  xnor_gate xnor_gate1(a[1], b[1], xnor1);
  
  and_gate and_gate2(a[0], b[0], eval_and1);
  // 1 - куда уйдёт, 2 - что пришло, 3 - запитка
  wire pre_out1;
  wire pre_out2;
  nmos nmos1(pre_out1, eval_and1, xnor1);
  pmos pmos1(pre_out1, gnd, xnor1);
  
  wire pre_second_bit1;
  wire pre_second_bit2;
  
  pmos pmos2(pre_second_bit1, a[0], a[1]);
  nmos nmos2(pre_second_bit1, gnd, a[1]);

  nmos nmos3(pre_second_bit2, b[0], a[1]);
  pmos pmos3(pre_second_bit2, gnd, a[1]);
  
  wire post_or;
  wire pre_second_bit;
  
  or_gate or_gate1(pre_second_bit2, pre_second_bit1, pre_second_bit);
  
  pmos pmos4(pre_out2, pre_second_bit, xnor1);
  nmos nmos4(pre_out2, gnd, xnor1);
  
  or_gate or_gate2(pre_out1, pre_out2, out[0]);
  output [1:0] out;  
  // TODO: implementation
endmodule

module ternary_max(a, b, out);
  input [1:0] a;
  input [1:0] b;
  

  supply1 pwr;
  supply0 gnd;
  
  or_gate or_gate1(a[1], b[1], out[1]);
  
  wire xnor0;
  wire eval_and1;

  xnor_gate xnor_gate0(a[0], b[0], xnor0);
  
  
  // 1 - куда уйдёт, 2 - что пришло, 3 - запитка
  wire pre_out1;
  wire pre_out2;

  nmos nmos1(pre_out1, a[0], xnor0);
  pmos pmos1(pre_out1, gnd, xnor0);
  
  wire xnor1;
  xnor_gate xnor_gate1(a[1], b[1], xnor1);


  // Верхний блок справа
  wire pre_pre_second_1;
  wire pre_pre_second_2;

  pmos pmos2(pre_pre_second_1, b[0], a[1]);
  nmos nmos2(pre_pre_second_1, gnd, a[1]);

  nmos nmos3(pre_pre_second_2, a[0], a[1]);
  pmos pmos3(pre_pre_second_2, gnd, a[1]);

  wire pre_second_11;
  or_gate or_gate2(pre_pre_second_1, pre_pre_second_2, pre_second_11);

  wire pre_second1;
  pmos pmos4(pre_second_1, pre_second_11, xnor1);
  nmos nmos4(pre_second_1, gnd, xnor1);

  // Нижний блок справа
  wire pre_pre_second_3;
  wire pre_pre_second_4;

  pmos pmos5(pre_pre_second_3, b[0], a[0]);
  nmos nmos5(pre_pre_second_3, gnd, a[0]);

  nmos nmos6(pre_pre_second_4, a[0], a[0]);
  pmos pmos6(pre_pre_second_4, gnd, a[0]);

  wire pre_second_22;
  or_gate or_gate3(pre_pre_second_3, pre_pre_second_4, pre_second_22);

  wire pre_second_2;
  nmos nmos7(pre_second_2, pre_second_22, xnor1);
  pmos pmos7(pre_second_2, gnd, xnor1);

  //ИЛИ для младшего бита
  wire second2;
  or_gate or_gate4(pre_second_1, pre_second_2, second2);

  pmos pmos8(pre_out2, second2, xnor0);
  nmos nmos8(pre_out2, gnd, xnor0);

  //вывод младшего бита
  or_gate or_gate5(pre_out1, pre_out2, out[0]);


  output [1:0] out; 
  // TODO: implementation
endmodule

module create_cloz(a1,a0,b1,b0,out);
  input a1;
  input a0;
  input b1;
  input b0;

  output out;


  wire firstAnd;
  and_gate and_gate1(a1, a0, firstAnd);

  wire secondAnd;
  and_gate and_gate2(b1, b0, secondAnd);

  and_gate and_gate3(firstAnd, secondAnd, out);

  

endmodule

module ternary_any(a, b, out);
  input [1:0] a;
  input [1:0] b;

  wire a1 = a[1];
  wire nota1;
  not_gate not_gate1(a1, nota1);

  wire a0 = a[0];
  wire nota0;
  not_gate not_gate2(a0, nota0);

  wire b1 = b[1];
  wire notb1;
  not_gate not_gate3(b1, notb1);
  
  wire b0 = b[0];
  wire notb0;
  not_gate not_gate4(b0, notb0);

  wire first_cloze;
  create_cloz create_cloz1(nota1, a0, b1, notb0, first_cloze);

  wire second_cloze;
  create_cloz create_cloz2(a1, nota0, notb1, b0, second_cloze);
  
  wire theard_cloze;
  create_cloz create_cloz3(a1, nota0, b1,notb0, theard_cloze);

  wire first_or;
  or_gate or_gate1(first_cloze, second_cloze,first_or);
  or_gate or_gate2(first_or, theard_cloze, out[1]);


  wire first_cloze2;
  create_cloz create_cloz4(nota1, nota0, b1, notb0, first_cloze2);

  wire second_cloze2;
  create_cloz create_cloz5(nota1, a0, notb1, b0, second_cloze2);
  
  wire theard_cloze2;
  create_cloz create_cloz6(a1, nota0, notb1, notb0, theard_cloze2);

  wire first_or2;
  or_gate or_gate3(first_cloze2, second_cloze2,first_or2);
  or_gate or_gate4(first_or2, theard_cloze2, out[0]);


  output [1:0] out;
  // TODO: implementation
endmodule

module ternary_consensus(a, b, out);
  input [1:0] a;
  input [1:0] b;
  
  supply1 pwr;
  supply0 gnd;

  // nmos(out[1], gnd, pwr);
  // nmos(out[0], gnd, pwr);
  
  //первый сравнитель с 0
  
  wire firstA;
  xnor_gate xnor_gate1(a[1], gnd, firstA);

  wire secondA;
  xnor_gate xnor_gate2(a[0], gnd, secondA);

  wire firstB;
  xnor_gate xnor_gate3(b[1], gnd, firstB);

  wire secondB;
  xnor_gate xnor_gate4(b[0], gnd, secondB);

  wire and01;
  and_gate and_gate1(firstA, secondA, and01);

  wire and02;
  and_gate and_gate2(firstB, secondB, and02);

  wire and0;
  and_gate and_gate3(and01, and02, and0);

  wire nul = 0;
  // проверка на совпадение с - -
  nmos nmos1(out[0], nul, and0);
  nmos nmos2(out[1], nul, and0);
  
  //увы не --
  // pmos pmos1( , , and0);


//второй сравнитель с 1
  wire fhreeA;
  xnor_gate xnor_gate5(a[1], pwr, fhreeA);

  wire fourA;
  xnor_gate xnor_gate6(a[0], gnd, fourA);

  wire fhreeB;
  xnor_gate xnor_gate7(b[1], pwr, fhreeB);

  wire fourB;
  xnor_gate xnor_gate8(b[0], gnd, fourB);

  wire and11;
  and_gate and_gate4(fhreeA, fourA, and11);

  wire and12;
  and_gate and_gate5(fhreeB, fourB, and12);

  wire and1;
  and_gate and_gate6(and11, and12, and1);

  wire its1;
  nmos nmos3(its1, pwr, and1);
  pmos pmos3(its1, gnd, and1);

  wire go10;
  pmos pmos4(go11, its1, and0);
  nmos nmos4(go11, gnd, and0);
  
  nmos nmos5(out[1], pwr, go11);
  nmos nmos6(out[0], gnd, go11);

  // pmos pmos11(out[1], out[1], go11);
  // pmos pmos12(out[0], out[0], go11);

  wire its0;
  pmos pmos7(its0, pwr, and1);
  nmos nmos7(its0, gnd, and1);

  wire go01;
  pmos pmos8(go01, its0, and0);
  nmos nmos8(go01, gnd, and0);
  
  nmos nmos9(out[1], gnd, go01);
  nmos nmos10(out[0], pwr, go01);

  output [1:0] out ;
  // TODO: implementation
endmodule
