library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;


entity Pipe3 is
    generic(DATA_WIDTH:natural:=8);
    port(
        input: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        c: out std_logic_vector((DATA_WIDTH - 1) downto 0);
        en_reg_adder: in std_logic;
        clk: in std_logic
    );
end Pipe3;

architecture rtl of Pipe3 is
    component Registrador is   
    generic(DATA_WIDTH: natural :=8);
        port (

            input    : in  std_logic_vector((DATA_WIDTH - 1) downto 0);
            output   : out  std_logic_vector((DATA_WIDTH - 1) downto 0);
            enable : in std_logic;
            clk : in std_logic
            
        );

    end component Registrador;

    -- Criando Sinais Auxiliares:
    signal aux_in : std_logic;
    signal aux_reg_out :std_logic;

begin
    -- Criando o fio que liga o aux_in
    aux_in <= input;

    -- Criando o Registrador:
    reg : Registrador
    port map(
        input=>aux_in,
        clk=>clk,
        enable=>en_reg_adder,
        output=>aux_reg_out

    );    

    c <= aux_reg_out;


end architecture rtl;
