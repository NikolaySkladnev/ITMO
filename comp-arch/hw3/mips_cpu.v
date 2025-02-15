`include "util.v"


module mips_cpu(clk, pc, pc_new, instruction_memory_a, instruction_memory_rd, data_memory_a, data_memory_rd, data_memory_we, data_memory_wd,
                register_a1, register_a2, register_a3, register_we3, register_wd3, register_rd1, register_rd2);
  // сигнал синхронизации
  input clk;
  // текущее значение регистра PC
  inout [31:0] pc; 
  // новое значение регистра PC (адрес следующей команды)
  output [31:0] pc_new;
  // we для памяти данных
  output data_memory_we;
  // адреса памяти и данные для записи памяти данных
  output [31:0] instruction_memory_a, data_memory_a, data_memory_wd;
  // данные, полученные в результате чтения из памяти
  inout [31:0] instruction_memory_rd, data_memory_rd;
  // we3 для регистрового файла
  output register_we3;
  // номера регистров
  output [4:0] register_a1, register_a2, register_a3;
  // данные для записи в регистровый файл
  output [31:0] register_wd3;
  // данные, полученные в результате чтения из регистрового файла
  inout [31:0] register_rd1, register_rd2;
  

  initial begin
      $dumpfile("dump.vcd");
      $dumpvars;
    end
  
  assign instruction_memory_a = pc;

  wire mem_to_reg;
  wire mem_write;
  wire branch;
  wire ALUSrc;
  wire RegDst;
  wire RegWrite;
  wire [2:0] ALUControl;
  wire j_jump;
  wire [31:0] new_jump_pc;
  wire jal_jump;
  wire jr_jump;

  control_unit control_unit1(instruction_memory_rd, PCPlus4, mem_to_reg, mem_write, branch, ALUSrc, RegDst, RegWrite, ALUControl, j_jump, new_jump_pc, jal_jump, jr_jump);
  
  assign register_a1 = instruction_memory_rd[25:21];
  assign register_a2 = instruction_memory_rd[20:16];
  
  wire [4:0] pre_register_a3;
  mux2_5 mux2_5_1(instruction_memory_rd[20:16], instruction_memory_rd[15:11], RegDst, pre_register_a3);
  mux2_5 mux2_5_2(pre_register_a3, 5'b11111, jal_jump, register_a3);

  wire [31:0] SignImm;
  sign_extend sign_extend_1(instruction_memory_rd[15:0], SignImm);
  wire [31:0] SignImm_after_shift;
  shl_2 shl_2_1(SignImm, SignImm_after_shift);

  wire [31:0] PCPlus4;
  adder adder1(pc, 4, PCPlus4);

  wire [31:0] PCBranch;
  adder adder2(SignImm_after_shift, PCPlus4, PCBranch);

  assign register_we3 = RegWrite;

  wire [31:0] SrcB;
  mux2_32 mux2_32_2(register_rd2, SignImm, ALUSrc, SrcB);

  wire [31:0] ALUResult;
  wire zero;
  alu alu_1(register_rd1, SrcB, ALUControl, ALUResult, zero);

  wire PCSrc;
  and_gate and_1(branch, zero, PCSrc);


  wire [31:0] pre_pc_new;
  mux2_32 mux2_32_1(PCPlus4, PCBranch, PCSrc, pre_pc_new);
  wire [31:0] pre_pre_pc_new;
  mux2_32 mux2_32_4(pre_pc_new, new_jump_pc, j_jump, pre_pre_pc_new);
  mux2_32 mux2_32_6(pre_pre_pc_new, register_rd1, jr_jump, pc_new);
  
  assign data_memory_a = ALUResult;
  assign data_memory_wd = register_rd2;
  assign data_memory_we = mem_write;

  wire [31:0] Result;
  mux2_32 mux2_32_3(ALUResult, data_memory_rd, mem_to_reg, Result);
  
  mux2_32 mux2_32_5(Result, PCPlus4, jal_jump, register_wd3);
    // TODO: implementation
endmodule


module control_unit (all_code, PCPlus4, mem_to_reg, mem_write, branch, ALUSrc, RegDst, RegWrite, ALUControl, j_jump, new_jump_pc, jal_jump, jr_jump);
  input [31:0] all_code;
  input [31:0] PCPlus4;
  wire [5:0] opcode;
  assign opcode = all_code[31:26];
  wire [5:0] funct;
  assign funct = all_code[5:0];
  output reg mem_to_reg;
  output reg mem_write;
  output reg branch;
  output reg ALUSrc;
  output reg RegDst;
  output reg RegWrite;
  output reg [2:0] ALUControl;
  output reg j_jump;
  output reg [31:0] new_jump_pc;
  output reg jal_jump;
  output reg jr_jump;
  
  always @(*) begin
      // $display("%d", PCPlus4 / 4);
      mem_to_reg = 1'b0;
      mem_write = 1'b0;
      branch = 1'b0;
      ALUSrc = 1'b0;
      RegDst = 1'b0;
      RegWrite = 1'b0;
      ALUControl = 3'b000;
      j_jump = 1'b0;
      new_jump_pc = {PCPlus4[31:28], all_code[25:0], 2'b00};
      jal_jump = 1'b0;
      jr_jump = 1'b0;

      if (opcode == 6'b000000 && funct == 6'b001000) begin
          $display("JR!");
          jr_jump = 1'b1;
        end

      if (opcode == 6'b000000 && funct != 6'b001000) begin
          RegWrite = 1'b1;
          RegDst = 1'b1;
          ALUSrc = 1'b0;
          branch = 1'b0;
          mem_write = 1'b0;
          mem_to_reg = 1'b0;
          
          if (funct == 6'b100000) begin
            $display("add!");
            ALUControl = 3'b010;
          end
          else if (funct == 6'b100010) begin
            $display("sub!");
            ALUControl = 3'b110;
          end
          else if (funct == 6'b100100) begin
            $display("and!");
            ALUControl = 3'b000;
          end
          else if (funct == 6'b100101) begin
            $display("or!");
            ALUControl = 3'b001;
          end
          else if (funct == 6'b101010) begin
            $display("slt!");
            ALUControl = 3'b111;
          end
      end

      else if(opcode == 6'b100011) begin
        $display("lw!");
        RegWrite = 1'b1;
        RegDst = 1'b0;
        ALUSrc = 1'b1;
        branch = 1'b0;
        mem_write = 1'b0;
        mem_to_reg = 1'b1;
        ALUControl = 3'b010; // OP = 00 --> 010 (add)
      end

      else if(opcode == 6'b101011) begin
        $display("sw!");
        RegWrite = 1'b0;
        RegDst = 1'b0;
        ALUSrc = 1'b1;
        branch = 1'b0;
        mem_write = 1'b1;
        mem_to_reg = 1'b0;
        ALUControl = 3'b010;
      end

      else if(opcode == 6'b000100) begin
        
        RegWrite = 1'b0;
        RegDst = 1'b0;
        ALUSrc = 1'b0;
        branch = 1'b1;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
        ALUControl = 3'b110;
        $display("beq! %d", branch);
      end

      else if(opcode == 6'b001000) begin
        $display("ADDI!");
        RegWrite = 1'b1;
        RegDst = 1'b0;
        ALUSrc = 1'b1;
        ALUControl = 3'b100;
        branch = 1'b0;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
      end
      
      else if(opcode == 6'b001100) begin
        $display("ANDI!");
        RegWrite = 1'b1;
        RegDst = 1'b0;
        ALUSrc = 1'b1;
        ALUControl = 3'b000;
        branch = 1'b0;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
      end

      else if(opcode == 6'b000101) begin
        $display("BNE!");
        RegWrite = 1'b0;
        RegDst = 1'b0;
        ALUSrc = 1'b0;
        ALUControl = 3'b011;
        branch = 1'b1;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
      end

      else if(opcode == 6'b000010) begin
        $display("J!");
        RegWrite = 1'b0;
        RegDst = 1'b0;
        ALUSrc = 1'b0;
        ALUControl = 3'b011;
        branch = 1'b0;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
        j_jump = 1'b1;
      end

      else if(opcode == 6'b000011) begin
        $display("Jal!");
        RegWrite = 1'b1;
        RegDst = 1'b0;
        ALUSrc = 1'b0;
        ALUControl = 3'b011;
        branch = 1'b0;
        mem_write = 1'b0;
        mem_to_reg = 1'b0;
        j_jump = 1'b1;
        jal_jump = 1'b1;
      end

     
  end
endmodule