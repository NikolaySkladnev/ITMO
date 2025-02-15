// модуль, который реализует расширенение
// 16-битной знаковой константы до 32-битной
module sign_extend(in, out);
  input [15:0] in;
  output [31:0] out;

  assign out = {{16{in[15]}}, in};
endmodule

// модуль, который реализует побитовый сдвиг числа
// влево на 2 бита
module shl_2(in, out);
  input [31:0] in;
  output [31:0] out;

  assign out = {in[29:0], 2'b00};
endmodule

// 32 битный сумматор
module adder(a, b, out);
  input [31:0] a, b;
  output [31:0] out;

  assign out = a + b;
endmodule

// 32-битный мультиплексор
module mux2_32(d0, d1, a, out);
  input [31:0] d0, d1;
  input a;
  output [31:0] out;
  assign out = a ? d1 : d0;
endmodule

// 5 - битный мультиплексор
module mux2_5(d0, d1, a, out);
  input [4:0] d0, d1;
  input a;
  output [4:0] out;
  assign out = a ? d1 : d0;
endmodule

module alu(a, b, control, out, zero);
  input [31:0] a;
  input [31:0] b;
  input [2:0] control;
  output reg [31:0] out;
  output reg zero; 
  
  always @(*)
  begin
      zero = 1'b0;
      if (control == 3'b110) begin
        if (a == b) begin
            zero = 1'b1;
        end
        // $display("%d, %d, %d", a, b, zero);
      end else begin
        if (control == 3'b011) begin
          if (a != b) begin
            zero = 1'b1;
          end else begin
            zero = 1'b0;
          end
        end else begin
          zero = 1'b0;
        end
      end
      // zero = 1'b0;
      // if (a == b) begin
      //   zero = 1'b1;        
      //   if (control == 3'b011) begin
      //     $display("EEEEEEEEEEEEEEEEEEEEEE");
      //     zero = 1'b0;        
      //   end
      // end
      // else begin
      //   if (control == 3'b011) begin
      //     $display("WWWWWW");
      //     zero = 1'b1;        
      //   end
      // end 

      // zero = ((a - b) == 0) ? 1'b1 : 1'b0;
      case(control)
        3'b000: 
          out = a & b ; 
        3'b001: 
          out = a | b ; 
        3'b010: 
          out = a + b; 
        3'b110: 
          out = a - b;
        3'b111: 
          out = (a == b) ? 32'd1 : 32'd0 ; 
        default: out = a + b;
      endcase
  end

endmodule

module and_gate (a, b, out);
  input a;
  input b;
  output out;

  assign out = a & b;
endmodule