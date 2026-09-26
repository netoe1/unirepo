-- Quartus II VHDL Template
-- Basic Shift Register

library ieee;
use ieee.std_logic_1164.all;

entity shifter4bits is
	port 
	(
		clk			: in std_logic;
		i		   	: in std_logic_vector(3 downto 0);
		sa	      	: in std_logic_vector(1 downto 0);
		s		   	: out std_logic_vector(3 downto 0)
	);

end entity;

architecture rtl of shifter4bits is
	--signal outMux1, outMux2, outMux3, outMux4: std_logic;
	signal outReg1, outReg2, outReg3, outReg4: std_logic;

begin

	process (clk)
	begin
		if (rising_edge(clk)) then
			if (sa = "00") then
				outReg1<= i(3);
				outReg2<= i(2);
				outReg3<= i(1);
				outReg4<= i(0);
			elsif (sa = "01") then 
				outReg1<= i(3);
				outReg2<= outReg1;
				outReg3<= outReg2;
				outReg4<= outReg3;
			elsif (sa = "10") then
				outReg1<= i(3);
				outReg2<= '0';
				outReg3<= outReg1;
				outReg4<= outReg2;
			else
				outReg1<= i(3);
				outReg2<= '0';
				outReg3<= '0';
				outReg4<= outReg1;
			end if;
		end if;
	end process;
	
	
	s(3)<=OutReg4;
	s(2)<=OutReg3;
	s(1)<=OutReg2;
	s(0)<=OutReg1;
	
	--s <= OutReg4 & OutReg3 & OutReg2 & OutReg1;
	
end rtl;






