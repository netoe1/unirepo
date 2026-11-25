library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

-- Sempre criar as interfaces como std_logic_vector;
entity somador8bits is

	generic(
		dataWidth: natural := 8
	);

	port (

		a    : in  std_logic_vector((dataWidth - 1) downto 0);
		b    : in  std_logic_vector((dataWidth - 1) downto 0);
        soma : out std_logic_vector((dataWidth - 1) downto 0)
	);

end entity somador8bits;

architecture rtl of somador8bits is
begin
-- 	Em circuitos digitais e VHDL, unsigned e signed são tipos de dados numéricos da biblioteca ieee.numeric_std usados para definir como o computador 
--  (ou FPGA) deve interpretar um conjunto de bits (um vetor) na hora de fazer contas matemáticas.
--  A diferença fundamental entre eles está na presença ou ausência de números negativos:
-- 	Unsigned (Sem Sinal)
-- 	Representa apenas números maiores ou iguais a zero (positivos). Todos os bits do vetor são usados para representar a magnitude (o valor) do número.
-- 	+/- Signed (Com Sinal)
-- 	Representa números positivos, zero e negativos. Ele utiliza um sistema matemático chamado Complemento de Dois.

    soma <= std_logic_vector(unsigned(a)+unsigned(b));
end architecture rtl;
