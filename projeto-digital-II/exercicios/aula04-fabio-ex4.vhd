--- Nesse arquivo, estamos criando um mux 2:1 com VHDL, com os exercícios do paulo

library ieee;
use ieee.std_logic_1164.all;

entity mux41 is
	port 
	(
                A: in std_logic;    -- Entrada A
                B: in std_logic;    -- Entrada B
                C: in std_logic;    -- Entrada C
                D: in std_logic;    -- Entrada D
                S: in std_logic_vector(1 downto 0);     -- Seletor
                M: out std_logic    -- Saída do MUX
	);

end entity;

architecture rtl of mux41 is
begin
        M <=    A when S = "00" else
                B when S = "01" else
                C when S = "10" else
                D;
end rtl;





