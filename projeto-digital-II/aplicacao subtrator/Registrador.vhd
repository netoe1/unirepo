library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity Registrador is   
    generic(DATA_WIDTH: natural :=8);
    port (

		input    : in  std_logic_vector((DATA_WIDTH - 1) downto 0);
		output   : out  std_logic_vector((DATA_WIDTH - 1) downto 0);
        enable : in std_logic;
        clk : in std_logic
        
	);

end entity Registrador;

architecture rtl of Registrador is
begin
    process(clk) is
    begin   
        if(rising_edge(clk)) then
            if(enable = '1') then
                output <= input;
            end if;
        end if;
    end process;
end architecture rtl;
