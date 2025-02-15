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


module or_gate(a,b,out);
  input wire a,b;
  output wire out;
  wire pre_out;
  
  nor_gate nor_gate1(a,b,pre_out);
  not_gate not_gate1(pre_out, out);
  
  
endmodule

module mass4_or(a,b,c,d, out);
  input a, b, c, d;
  wire [5:0] pre_out;
  
  or_gate or_gate1(a, b, pre_out[0]);
  or_gate or_gate2(c, d, pre_out[1]);
  or_gate or_gate5(pre_out[0], pre_out[1], out);
  output out;
endmodule

module and_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(a, b, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module and4_gate(a_control, b_control, c_control, D, out);
  input wire a_control, b_control, c_control, D;
  output out;
  
  wire first_and;
  and_gate and_gate1(a_control, b_control, first_and);

  wire second_and;
  and_gate and_gate2(c_control, D, second_and);

  and_gate and_gate3(first_and, second_and, out);


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

module multiplecsor4 (control, first4, second4, third4, four4, out);

  input [1:0] control;

  supply0 gnd;

  input first4, second4, third4, four4;
  
  wire controlB = control[1];
  wire not_controlB;
  not_gate not_gate2(controlB, not_controlB);
 
  wire controlC = control[0];
  wire not_controlC;
  not_gate not_gate3(controlC, not_controlC);
  supply1 pwr;

  wire finish11;
  and4_gate and4_gate5(pwr, controlB, controlC, four4, finish11);

  wire finish10;
  and4_gate and4_gate6(pwr, controlB, not_controlC, third4, finish10);

  wire finish01;
  and4_gate and4_gate7(pwr, not_controlB, controlC, second4, finish01);

  wire finish00;
  and4_gate and4_gate8(pwr, not_controlB, not_controlC, first4, finish00);

  mass4_or mass4_or1(finish00, finish01, finish10, finish11, out);

  output out;
endmodule


module implication_mos(a, b, out);
  input a, b;
  output out;
  wire [1:0] control;
  assign control[0] = b;
  assign control[1] = a;
  supply0 gnd;
  supply1 pwr;
  multiplecsor4 multiplecsor4_1(control, pwr, pwr, gnd, pwr, out);
endmodule

// module test ();
//   wire a = 0; 
//   wire b = 0;
//   wire out;
//   supply0 gnd;
//   supply1 pwr;
//   implication_mos implication_mos1(pwr, pwr, out);
//   initial begin
//     #2 $display(out); 
//   end    
// endmodule