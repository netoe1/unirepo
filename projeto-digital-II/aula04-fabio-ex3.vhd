--- Nesse arquivo, estamos criando um mux 2:1 com VHDL, com os exercícios do paulo

library ieee;
use ieee.std_logic_1164.all;

entity mux is
	port 
	(
        A: in std_logic;    -- Entrada A
        B: in std_logic;    -- Entrada B
        S: in std_logic;    -- Seletor
        M: out std_logic    -- Saída do MUX
	);

end entity;

architecture rtl of mux is
begin
    -- M será igual a A quando S for LOW, senão será B;
    M <=    A when S = '0'
            else B;
end rtl;





