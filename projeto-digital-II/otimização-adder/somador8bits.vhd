library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity somador8bits is
	port (
		a    : in  std_logic_vector(7 downto 0);
		b    : in  std_logic_vector(7 downto 0);
        soma : out std_logic_vector(7 downto 0);
		cout : out std_logic
	);
end entity somador8bits;

architecture rtl of somador8bits is
begin
    soma <= std_logic_vector(unsigned(a,9)+unsigned(b,9));
	cout <= soma(9);
end architecture rtl;
