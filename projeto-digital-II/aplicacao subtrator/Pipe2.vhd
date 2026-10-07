library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;


entity Pipe2 is
    generic(DATA_WIDTH: natural:=8);
    port(

        -- Entradas do Pipe:
            a: in   std_logic_vector((DATA_WIDTH - 1) downto 0);
            b: in   std_logic_vector((DATA_WIDTH - 1) downto 0);

        -- Sinais de Controle:
        ctrl_mux1: in   std_logic;
        ctrl_mux2: in   std_logic;

        -- Entradas do mux:
        a1_in_mux1: in   std_logic_vector((DATA_WIDTH - 1) downto 0) :='1';
        b1_in_mux2: in   std_logic_vector((DATA_WIDTH - 1) downto 0); -- Entrada do mux2_a0, que é retroalimentado.

        -- Saídas:
        out_adder: out   std_logic_vector(DATA_WIDTH - 1 downto 0)

       );
end entity;

architecture rtl of Pipe2 is

    -- Trazendo o componente do adder:
    component Somador is
        generic(DATA_WIDTH: natural:=8);
        
        port(
            a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
            b: in std_logic_vector((DATA_WIDTH - 1) downto 0);   
            soma: out std_logic_vector((DATA_WIDTH - 1) downto 0)
        );

    end component Somador;

    -- Trazendo o Componente Mux.
    component Mux is
        generic(DATA_WIDTH: natural:=8);
        port(
            ctrl_mux: in std_logic;
            a: in std_logic_vector((DATA_WIDTH - 1) downto 0);
            b: in std_logic_vector((DATA_WIDTH - 1) downto 0);
            out_mux: out std_logic_vector((DATA_WIDTH - 1) downto 0)
        );
    end component Mux;



    -- Fios intermediários para colocar nos inputs do adder.

    -- Entrada do mux:
    signal in_mux1:std_logic_vector((DATA_WIDTH-1) downto 0);
    signal in_mux2:std_logic_vector((DATA_WIDTH-1) downto 0);
    signal in_retro_b1_mux2 :std_logic_vector((DATA_WIDTH-1) downto 0);

    -- Saída do Mux.
    signal out_mux1:std_logic_vector((DATA_WIDTH-1) downto 0);
    signal out_mux2:std_logic_vector((DATA_WIDTH-1) downto 0);

    -- Saída do Adder:
    signal aux_out_adder:std_logic_vector((DATA_WIDTH-1) downto 0);
begin
    -- Asssinalando as entradas para os fios até o mux1 e mux 2:
    -- Pega o input do bloco pipe e coloca fios até os muxes.
    in_mux1 <= a;
    in_mux2 <= b;
    in_retro_b1_mux2 <= b1_in_mux2;
    
    -- Criando os muxes em paralelo
    mux1: Mux port map(
        a=>in_mux1,
        b=>a1_in_mux1,
        ctrl_mux=>ctrl_mux1,
        out_mux=>out_mux1);

    mux2: Mux port map(
        a=>in_mux2,
        b=>in_retro_b1_mux2,
        ctrl_mux=>ctrl_mux2,
        out_mux=>out_mux2);


    adder: Somador
    port map(
        a=>out_mux1,
        b=>out_mux2,
        soma=>aux_out_adder
    );

    out_adder <= aux_out_adder;

end architecture rtl;

