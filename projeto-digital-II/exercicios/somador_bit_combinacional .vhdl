-- Usando a biblioteca do fabio:
library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_signed.all;

entity ent_somador_4_bits is 
    port(
        a:      in std_logic;
        b:      in std_logic;
        c:      in std_logic;
        cout:   out std_logic
    
    );
    end ent_somador_4_bits;
architecture behavior of ent_somador_4_bits is
begin 


    cout <=    ((not a)    and (b)     and  (c))     or
                (a)         and (not b) and (c)      or
                (a)         and (b)     and (not c)  or
                (a)         and (b)     and (c);
end behavior;       