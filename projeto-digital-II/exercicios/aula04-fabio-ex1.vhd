-- Quartus II VHDL Template
-- Basic Shift Register

library ieee;
use ieee.std_logic_1164.all;

entity ex1 is
	port 
	(
		in1: in std_logic;
        in2: in std_logic;
        out1: out std_logic
	);

end entity;

architecture rtl of ex1 is
begin
	out1 <= (not (in1 or in2)) xor (in2); 
	
end rtl;





