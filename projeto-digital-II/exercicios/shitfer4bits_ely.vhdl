-- 1. Declaração de Bibliotecas
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- 2. Entidade (Interface do Circuito)
entity Shifter4Bits is
    Port ( 
        input_shifter: in STD_LOGIC_VECTOR(3 downto 0);
        controle_mux: in STD_LOGIC_VECTOR (1 downto 0);
        output_shifter: out STD_LOGIC_VECTOR(3 downto 0)
    );
end ;

-- 3. Arquitetura (Comportamento ou Lógica)
architecture Behavior of Shifter4Bits is 

    signal out_mux1: std_logic := '0';
    signal out_mux2: std_logic := '0';
    signal out_mux3: std_logic := '0';
    signal out_mux4: std_logic := '0';

    signal out_reg1: std_logic := '0';
    signal out_reg2: std_logic := '0';
    signal out_reg3: std_logic := '0';
    signal out_reg4: std_logic := '0';

begin
    --- Criando os sinais de controle:

    

    process(input_shifter,out_reg1,out_reg2,out_reg3,controle_mux)
    begin

        --- Criando o MUX com os sinais de controle;
        if(controle_mux = "00") then
            out_mux1 <= input_shifter(3);
            out_mux2 <= input_shifter(2);
            out_mux3 <= input_shifter(1);
            out_mux4 <= input_shifter(0);

        elsif (controle_mux = "01") then
            out_mux1 <= input_shifter(3);
            out_mux2 <= out_reg1;
            out_mux3 <= input_shifter(1);
            out_mux4 <= input_shifter(0);

        elsif (controle_mux = "10") then
            out_mux1 <= '0';
            out_mux2 <= '0';
            out_mux3 <= out_reg1;
            out_mux4 <= out_reg2;
        else
          
        end if;
            
    end process;

end architecture;