library ieee;
use ieee.std_logic_1164.all;

entity Mux is
    generic(DATA_WIDTH: natural:=8);
    port(
        ctrl_mux1: in std_logic;
        x: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        out_mux: out std_logic_vector((DATA_WIDTH - 1) downto 0)
    );
end Mux;

architecture rtl of Mux is 
begin
    out_mux <= x when ctrl_mux1 = '0' else (0 =>'1', others=> '0'); 
end rtl;