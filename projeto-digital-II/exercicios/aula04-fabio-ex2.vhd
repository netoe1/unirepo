
library ieee;
use ieee.std_logic_1164.all;

entity ex2 is
	port 
	(
		in1: in std_logic;
        in2: in std_logic;
        in3: in std_logic;
        out1: out std_logic;
        out2: out std_logic
	);

end entity;

architecture rtl of ex2 is

    --- Criando signal antes do begin:
    signal or_with_in1_in2: std_logic;
begin
    --- out1 <= (not(in1 or in2)) xor in3;
    --- out2 <= in1 or in2;

    --- Vou otimizar o circuito com signal:
    or_with_in1_in2 <= in1 or in2;
    out1 <= not(or_with_in1_in2) xor in3;
    out2 <= or_with_in1_in2;
end rtl;





