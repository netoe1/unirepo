library ieee;
use ieee.std_logic_1164.all;

entity Mux is
    generic(DATA_WIDTH: natural:=8);
    port(
        ctrl_mux: in std_logic;
        a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        b: in std_logic_vector((DATA_WIDTH - 1) downto 0);
        out_mux: out std_logic_vector((DATA_WIDTH - 1) downto 0)
    );
end Mux;

architecture rtl of Mux is 
    -- Criando aux_out para tirar o resultado final do process;
    signal aux_out: std_logic_vector((DATA_WIDTH - 1) downto 0);
begin
    process(a,b,ctrl_mux,out_mux)
    begin
        if(ctrl_mux = '0') then
            aux_out <= a;
        else
            aux_out <= b;
        end if;
    end process;

    -- Colocando o valor de fora.
    out_mux <= aux_out;

end rtl;