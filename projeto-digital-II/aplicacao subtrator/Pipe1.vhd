library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;


entity Pipe1 is 
    -- Realizando as entradas e saidas do Pipe1
    generic(DATA_WIDTH:natural:=8);
    port(
        pipe1_in_a:    in std_logic_vector     ((DATA_WIDTH - 1) downto 0);
        pipe1_in_b:    in std_logic_vector     ((DATA_WIDTH - 1) downto 0);
        en_inputs:  in std_logic;
        pipe1_out_a:      out std_logic_vector     ((DATA_WIDTH - 1) downto 0);
        pipe1_out_b:      out std_logic_vector     ((DATA_WIDTH - 1) downto 0);
        pipe1_clk:        in std_logic
    );
end Pipe1;

architecture rtl of Pipe1 is

-- Importando o Componente Registrador, com o data width igual

component Registrador is   
    generic(DATA_WIDTH: natural :=8);
    port (

		input    : in  std_logic_vector((DATA_WIDTH - 1) downto 0);
		output   : out  std_logic_vector((DATA_WIDTH - 1) downto 0);
        enable : in std_logic;
        clk : in std_logic
        
	);

end component Registrador;

signal sig_out_reg_a:std_logic_vector((DATA_WIDTH - 1) downto 0); 
signal sig_out_reg_b:std_logic_vector((DATA_WIDTH - 1) downto 0);
begin
    -- Criando o Registrador A:
    pipe_reg_a: Registrador 
        port map(
            input=>pipe1_in_a,
            enable=> en_inputs,
            clk=>pipe1_clk,
            output=>sig_out_reg_a
            );

    -- Criando o Registrador B
    pipe_reg_b: Registrador 
        port map(
            input=>pipe1_in_b,
            enable=> en_inputs,
            clk=>pipe1_clk,
            output=>sig_out_reg_b
            );

        
    pipe1_out_a <= sig_out_reg_a;
    pipe1_out_b <= not sig_out_reg_b;

        
end architecture rtl;