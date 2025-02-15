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


module xor_gate (a, b, out);

  input a,b;

  wire [3:0] pre_out;

  or_gate or_gate1(a, b, pre_out[0]);
  and_gate and_gate1(a,b, pre_out[1]);
  not_gate not_gate1(pre_out[1], pre_out[2]);

  and_gate and_gate2(pre_out[0], pre_out[2], out);
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

module not4_gate(a,out);
  input [3:0] a;

  not_gate not_gate1(a[0], out[0]);
  not_gate not_gate2(a[1], out[1]);
  not_gate not_gate3(a[2], out[2]);
  not_gate not_gate4(a[3], out[3]);

  output [3:0] out;
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

module mass_and(a,b, out);
  input [3:0] a, b;

  and_gate and_gate1(a[0], b[0], out[0]);
  and_gate and_gate2(a[1], b[1], out[1]);
  and_gate and_gate3(a[2], b[2], out[2]);
  and_gate and_gate4(a[3], b[3], out[3]);

  output [3:0] out;
endmodule

module mass_or(a,b, out);
  input [3:0] a, b;

  or_gate or_gate1(a[0], b[0], out[0]);
  or_gate or_gate2(a[1], b[1], out[1]);
  or_gate or_gate3(a[2], b[2], out[2]);
  or_gate or_gate4(a[3], b[3], out[3]);

  output [3:0] out;
endmodule

module mass8_or(a,b,c,d,e,f,g,h, out);
  input a, b, c,d,e,f,g,h;
  wire [5:0] pre_out;
  
  or_gate or_gate1(a, b, pre_out[0]);
  or_gate or_gate2(c, d, pre_out[1]);
  or_gate or_gate3(e, f, pre_out[2]);
  or_gate or_gate4(g, h, pre_out[3]);

  or_gate or_gate5(pre_out[0], pre_out[1], pre_out[4]);
  or_gate or_gate6(pre_out[2], pre_out[3], pre_out[5]);
  or_gate or_gate7(pre_out[4], pre_out[5], out);

  output out;
endmodule

module mass4_or(a,b,c,d, out);
  input a, b, c, d;
  wire [5:0] pre_out;
  
  or_gate or_gate1(a, b, pre_out[0]);
  or_gate or_gate2(c, d, pre_out[1]);
  or_gate or_gate5(pre_out[0], pre_out[1], out);
  output out;
endmodule


module summator (a, b, perenos, out, new_perenos);
  input a, b, perenos;

  wire [3:0] pre_out;
  xor_gate xor_gate1(a, b, pre_out[0]);
  and_gate and_gate1(a, b, pre_out[1]);

  xor_gate xor_gate2(pre_out[0], perenos, out);
  and_gate and_gate2(pre_out[0], perenos, pre_out[2]);

  or_gate or_gate1(pre_out[1], pre_out[2], new_perenos);

  output out, new_perenos;
  
endmodule



module subtraction (a, b, perenos, out, new_perenos);
  input a, b, perenos;
  
  supply1 pwr;

  wire new_b;
  xor_gate xor_gate1(b, pwr, new_b);
  summator summator1(a, new_b, perenos, out, new_perenos);

  output out, new_perenos;
endmodule


module summator_all (a, b, perenos, T, out, new_perenos);
  input a, b, perenos, T;

  wire new_b;
  xor_gate xor_gate7(b,T, new_b);
  wire [3:0] pre_out;
  xor_gate xor_gate1(a, new_b, pre_out[0]);
  and_gate and_gate1(a, new_b, pre_out[1]);

  xor_gate xor_gate2(pre_out[0], perenos, out);
  and_gate and_gate2(pre_out[0], perenos, pre_out[2]);

  or_gate or_gate1(pre_out[1], pre_out[2], new_perenos);

  output out, new_perenos;
  
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




module multiplecsor8 (control, slt, substr, summa, nm_or, m_or, nm_and, m_and, out);

  input [2:0] control;

  supply0 gnd;

  input slt, substr, summa, nm_or, m_or, nm_and, m_and;
  

  wire controlA = control[2];
  wire not_controlA;
  not_gate not_gate1(controlA, not_controlA);

  wire controlB = control[1];
  wire not_controlB;
  not_gate not_gate2(controlB, not_controlB);
 
  wire controlC = control[0];
  wire not_controlC;
  not_gate not_gate3(controlC, not_controlC);

  wire finish111;
  and4_gate and4_gate1(controlA, controlB, controlC, gnd, finish111);
  
  wire finish110;
  and4_gate and4_gate2(controlA, controlB, not_controlC, slt, finish110);

  wire finish101;
  and4_gate and4_gate3(controlA, not_controlB, controlC, substr, finish101);

  wire finish100;
  and4_gate and4_gate4(controlA, not_controlB, not_controlC, summa, finish100);

  wire finish011;
  and4_gate and4_gate5(not_controlA, controlB, controlC, nm_or, finish011);

  wire finish010;
  and4_gate and4_gate6(not_controlA, controlB, not_controlC, m_or, finish010);

  wire finish001;
  and4_gate and4_gate7(not_controlA, not_controlB, controlC, nm_and, finish001);

  wire finish000;
  and4_gate and4_gate8(not_controlA, not_controlB, not_controlC, m_and, finish000);

  mass8_or mass8_or1(finish000, finish001, finish010, finish011, finish100, finish101, finish110, finish111, out);

  output out;
endmodule




module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции
  supply0 gnd;
  initial begin
    // Системная функция $display выводит текст на экран во время симуляции.
    $display("Hello world");
    $dumpfile("dump.vcd");
    $dumpvars;
  end
  wire [3:0] M_and; // a & b
  mass_and mass_and1(a,b, M_and);
  wire [3:0] NM_and; // !(a & b)
  not4_gate not4_gate1(M_and, NM_and);

  wire [3:0] M_or; // a | b
  mass_or mass_or1(a,b, M_or);
  wire [3:0] NM_or; // !(a | b)
  not4_gate not4_gate2(M_or, NM_or);
  

  //делаем сумматор
  wire [3:0] summa;
  wire [3:0] perenos;
  summator summator1(a[0], b[0], gnd, summa[0], perenos[0]);
  summator summator2(a[1], b[1], perenos[0], summa[1], perenos[1]);
  summator summator3(a[2], b[2], perenos[1], summa[2], perenos[2]);
  summator summator4(a[3], b[3], perenos[2], summa[3], perenos[3]);

  //делаем вычитание на сумматоре

  wire [3:0] substr;
  wire [3:0] perenos_subst;
  supply1 pwr;
  subtraction subtraction5(a[0], b[0], pwr, substr[0], perenos_subst[0]);
  subtraction subtraction6(a[1], b[1], perenos_subst[0], substr[1], perenos_subst[1]);
  subtraction subtraction7(a[2], b[2], perenos_subst[1], substr[2], perenos_subst[2]);
  subtraction subtraction8(a[3], b[3], perenos_subst[2], substr[3], perenos_subst[3]);

  // slt
  wire slt;
  wire x = substr[3];
  wire y;
  wire z;

  wire not_b;
  wire not_a;
  not_gate not_gate4(b[3], not_b);
  not_gate not_gate5(a[3], not_a);

  and_gate and_gate5(a[3], not_b, y);
  and_gate and_gate6(not_a, b[3], z);

  wire gg;
  
  nmos nmos1(gg, pwr, y);
  pmos pmos1(gg, gnd, y);

  pmos pmos2(slt, gg, x);

  wire qq;
  pmos pmos3(qq, pwr, z);
  nmos nmos2(qq, gnd, z);

  nmos nmos3(slt, qq, x);



  // я должен получить substr[3] == 0 и проверить, что данные разные
  // 1 - куда уйдёт, 2 - что пришло, 3 - запитка
  // nmos pmos1(slt, pwr, slt_substr[3]);
  // pmos nmos1(slt, gnd, slt_substr[3]);

  // готовый мультиплексор, добавлять в начало переменные
  multiplecsor8 multiplecsor81(control, gnd, substr[3], summa[3], NM_or[3], M_or[3], NM_and[3], M_and[3], res[3]);
  multiplecsor8 multiplecsor82(control, gnd, substr[2], summa[2], NM_or[2], M_or[2], NM_and[2], M_and[2], res[2]);
  multiplecsor8 multiplecsor83(control, gnd, substr[1], summa[1], NM_or[1], M_or[1], NM_and[1], M_and[1], res[1]);
  multiplecsor8 multiplecsor84(control, slt, substr[0], summa[0], NM_or[0], M_or[0], NM_and[0], M_and[0], res[0]);
 
  output [3:0] res; // Результат
  // TODO: implementation
endmodule

module d_latch(clk, d, we, q);
  input clk; // Сигнал синхронизации
  input d; // Бит для записи в ячейку
  input we; // Необходимо ли перезаписать содержимое ячейки

  output reg q; // Сама ячейка
  // Изначально в ячейке хранится 0
  initial begin
    q <= 0;
  end
  // Значение изменяется на переданное на спаде сигнала синхронизации
  always @ (negedge clk) begin
    // Запись происходит при we = 1
    if (we) begin
      q <= d;
    end
  end
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data, we);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл
  input we; // Необходимо ли перезаписать содержимое регистра


  wire not_we_addr1;
  not_gate not_gate1(we_addr[1], not_we_addr1);

  wire not_we_addr2;
  not_gate not_gate2(we_addr[0], not_we_addr2);
  
  wire first4;
  and_gate and_gate1(not_we_addr1, not_we_addr2, first4);

  wire second4;
  and_gate and_gate2(not_we_addr1, we_addr[0], second4);
  
  wire third4;
  and_gate and_gate3(we_addr[1], not_we_addr2, third4);
  
  wire four4;
  and_gate and_gate4(we_addr[1], we_addr[0], four4);
  
  wire [3:0] data1;
  
  wire rewrite1;
  and_gate and_gate5(we, first4, rewrite1);

  d_latch d1(clk, we_data[0], rewrite1, data1[0]);
  d_latch d2(clk, we_data[1], rewrite1, data1[1]);
  d_latch d3(clk, we_data[2], rewrite1, data1[2]);
  d_latch d4(clk, we_data[3], rewrite1, data1[3]);

  wire [3:0] data2;
  wire rewrite2;
  and_gate and_gate6(we, second4, rewrite2);

  d_latch d5(clk, we_data[0], rewrite2, data2[0]);
  d_latch d6(clk, we_data[1], rewrite2, data2[1]);
  d_latch d7(clk, we_data[2], rewrite2, data2[2]);
  d_latch d8(clk, we_data[3], rewrite2, data2[3]);

  wire [3:0] data3;
  wire rewrite3;
  and_gate and_gate7(we, third4, rewrite3);

  d_latch d9(clk, we_data[0], rewrite3, data3[0]);
  d_latch d10(clk, we_data[1], rewrite3, data3[1]);
  d_latch d11(clk, we_data[2], rewrite3, data3[2]);
  d_latch d12(clk, we_data[3], rewrite3, data3[3]);


  wire [3:0] data4;
  wire rewrite4;
  and_gate and_gate8(we, four4, rewrite4);

  d_latch d13(clk, we_data[0], rewrite4, data4[0]);
  d_latch d14(clk, we_data[1], rewrite4, data4[1]);
  d_latch d15(clk, we_data[2], rewrite4, data4[2]);
  d_latch d16(clk, we_data[3], rewrite4, data4[3]);

  multiplecsor4 multiplecsor4_1(rd_addr, data1[0], data2[0], data3[0], data4[0], rd_data[0]);
  multiplecsor4 multiplecsor4_2(rd_addr, data1[1], data2[1], data3[1], data4[1], rd_data[1]);
  multiplecsor4 multiplecsor4_3(rd_addr, data1[2], data2[2], data3[2], data4[2], rd_data[2]);
  multiplecsor4 multiplecsor4_4(rd_addr, data1[3], data2[3], data3[3], data4[3], rd_data[3]);


  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  // TODO: implementation
endmodule

module counter(clk, addr, control, immediate, data);
  input clk; // Сигнал синхронизации
  input [1:0] addr; // Номер значения счетчика которое читается или изменяется
  input [3:0] immediate; // Целочисленная константа, на которую увеличивается/уменьшается значение счетчика
  input control; // 0 - операция инкремента, 1 - операция декремента

  // забираем текущую информацию из всех ячеек
  supply1 pwr;

  wire [3:0] first_sum;
  wire [3:0] itog_sum;

  register_file register_file_1(clk, addr, addr, itog_sum, first_sum, pwr);

  wire [3:0] perenos;
  summator_all summator_all_1(first_sum[0], immediate[0], control, control, itog_sum[0], perenos[0]);
  summator_all summator_all_2(first_sum[1], immediate[1], perenos[0], control, itog_sum[1], perenos[1]);
  summator_all summator_all_3(first_sum[2], immediate[2], perenos[1], control, itog_sum[2], perenos[2]);
  summator_all summator_all_4(first_sum[3], immediate[3], perenos[2], control, itog_sum[3], perenos[3]);

  assign data = first_sum;

  // supply1 pwr;
  output [3:0] data; // Данные из значения под номером addr, подающиеся на выход
  // TODO: implementation
endmodule
