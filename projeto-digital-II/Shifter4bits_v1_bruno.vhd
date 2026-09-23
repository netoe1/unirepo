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

	OutMux1 <= i(3);
	
	OutMux4 <= i(0)    when sa = "00" else
				  outReg3 when sa = "01" else
				  outReg2 when sa = "10" else
				  outReg1;

	OutMux2 <= i(2) when sa = "00" else
				  outReg1 when sa = "01" else
				  '0' when sa = "10" else
				  '0';			  
	
	OutMux3 <=  i(1) when sa = "00" else
					OutReg2 when sa = "01" else
					OutReg1 when sa = "10" else
					'0';
					
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





