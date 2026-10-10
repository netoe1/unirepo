vhdl
--------------------------------------------------------------------------------
-- Engenheiro: Seu Nome
-- Projeto: Template de Máquina de Estados Finitos (FSM)
-- Descrição: Modelo padrão de FSM utilizando o estilo recomendado de 
--            dois processos (Sequencial + Combinacional).
--------------------------------------------------------------------------------

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity fsm_template is
    Port (
        clk      : in  STD_LOGIC;                     -- Sinal de Clock
        rst      : in  STD_LOGIC;                     -- Reset Ativo em Alto ('1')
        input_x  : in  STD_LOGIC;                     -- Exemplo de entrada
        output_y : out STD_LOGIC                      -- Exemplo de saída
    );
end fsm_template;

architecture Behavioral of fsm_template is

    -- Definição do tipo enumerado para os estados da FSM
    type type_state is (state1, state2, state3, state4);
    
    -- Sinais para armazenar o estado atual e o próximo estado
    signal state_reg, state_next : type_state;

begin

    ----------------------------------------------------------------------------
    -- 1. PROCESSO SEQUENCIAL (Registrador de Estado)
    -- Responsável por atualizar o estado atual na borda de subida do clock.
    ----------------------------------------------------------------------------
    process(clk, rst)
    begin
        if rst = '1' then
            state_reg <= state1; -- Estado inicial após o reset
        elsif rising_edge(clk) then
            state_reg <= state_next;
        end if;
    end process;

    ----------------------------------------------------------------------------
    -- 2. PROCESSO COMBINACIONAL (Lógica de Próximo Estado e Saídas)
    -- Avalia o estado atual e as entradas para determinar o comportamento da FSM.
    ----------------------------------------------------------------------------
    process(state_reg, input_x)
    begin
        -- Atribuições padrão (Evita a criação de Latches indesejados)
        state_next <= state_reg;
        output_y   <= '0';

        case state_reg is
        
            when state1 =>
                output_y <= '0';
                if input_x = '1' then
                    state_next <= state2;
                end if;

            when state2 =>
                output_y <= '1';
                if input_x = '0' then
                    state_next <= state3;
                end if;

            when state3 =>
                output_y <= '1';
                state_next <= state4;

            when state4 =>
                output_y <= '0';
                if input_x = '1' then
                    state_next <= state1;
                end if;

            when others =>
                state_next <= state1;
                
        end case;
    end process;

end Behavioral;