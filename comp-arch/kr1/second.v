module mux_8_1(a, d, out);
  input [2:0] a;
  input [0:7] d;
  output out;
  reg out;
  always @ (a or d) begin
    case (a)
      3'b000: out <= d[0];
      3'b001: out <= d[1];
      3'b010: out <= d[2];
      3'b011: out <= d[3];
      3'b100: out <= d[4];
      3'b101: out <= d[5];
      3'b110: out <= d[6];
      3'b111: out <= d[7];
    endcase
  end
endmodule
 
module median_mux(a, b, c, out);
    input a, b, c;
    wire [2:0] control;
    assign control[0] = c;
    assign control[1] = b;
    assign control[2] = a;
    
    wire [7:0] d;
    assign d[7] = 0;
    assign d[6] = 1;
    assign d[5] = 1;
    assign d[4] = 0;
    assign d[3] = 1;
    assign d[2] = 0;
    assign d[1] = 0;
    assign d[0] = 1;
    
    mux_8_1 mux_8_11(control, d, out);
    output out;
    //TODO: implementation
endmodule

// module test ();
//     supply0 gnd;
//     supply1 pwr;
//     wire out;
//     median_mux median_mux1(pwr, pwr, pwr, out);

//     initial begin
//         #2 $display(out);
//     end
// endmodule