-- Quartus II VHDL Template
-- Basic Shift Register

library ieee;
use ieee.std_logic_1164.all;

entity shifter4bits is
	port 
	(
		clk		: in std_logic;
		i		   : in std_logic_vector(3 downto 0);
		sa	      : in std_logic_vector(1 downto 0);
		s		   : out std_logic_vector(3 downto 0)
	);

end entity;

architecture rtl of shifter4bits is
	signal outMux1, outMux2, outMux3, outMux4: std_logic;
	signal outReg1, outReg2, outReg3, outReg4: std_logic;

begin

	process (i, outReg1, outReg2, outReg3, sa)
	begin
		if (sa = "00") then
			outMux1<= i(3);
			outMux2<= i(2);
			outMux3<= i(1);
			outMux4<= i(0);
		elsif (sa = "01") then 
			outMux1<= i(3);
			outMux2<= outReg1;
			outMux3<= outReg2;
			outMux4<= outReg3;
		elsif (sa = "10") then
			outMux1<= i(3);
			outMux2<= '0';
			outMux3<= outReg1;
			outMux4<= outReg2;
		else
			outMux1<= i(3);
			outMux2<= '0';
			outMux3<= '0';
			outMux4<= outReg1;
		end if;
	end process;
	
					
	process (clk)
	begin
		if (rising_edge(clk)) then
			OutReg1 <= OutMux1;
			OutReg2 <= OutMux2;
			OutReg3 <= OutMux3;
			OutReg4 <= OutMux4;
		end if;
	end process;
	
	s(3)<=OutReg4;
	s(2)<=OutReg3;
	s(1)<=OutReg2;
	s(0)<=OutReg1;
	
	--s <= OutReg4 & OutReg3 & OutReg2 & OutReg1;
	
end rtl;





