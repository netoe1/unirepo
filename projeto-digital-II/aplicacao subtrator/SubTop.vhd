library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;


entity SubTop is
    generic(DATA_WIDTH: natural:= 8);
    port(
        st_ctr_mux1: in std_logic;
        st_a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        st_b: in std_logic_vector((DATA_WIDTH - 1) downto 0)
        
    );
end SubTop;

architecture rtl of SubTop is
    component Mux is
        generic(DATA_WIDTH: natural:=8);
        port(
            ctrl_mux1: in std_logic;
            x: in std_logic_vector((DATA_WIDTH - 1) downto 0);
            out_mux: out std_logic_vector((DATA_WIDTH - 1) downto 0)
        );
    end component;

    component Somador is
        generic(DATA_WIDTH: natural:=8);
        
        port(
            a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
            b: in std_logic_vector((DATA_WIDTH - 1) downto 0);   
            soma: out std_logic_vector((DATA_WIDTH - 1) downto 0)
        );

    end component;
begin  
    
end rtl;




