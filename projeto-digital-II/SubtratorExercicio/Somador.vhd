library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity Somador is
    generic(DATA_WIDTH: natural:=8);
    
    port(
        a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        b: in std_logic_vector((DATA_WIDTH - 1) downto 0);   
        soma: out std_logic_vector((DATA_WIDTH - 1) downto 0)
    );

end Somador;

architecture rtl of Somador is
begin
    soma <= std_logic_vector(unsigned(a) + unsigned(b)); 
end rtl;