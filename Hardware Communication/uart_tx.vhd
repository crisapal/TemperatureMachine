library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED;

entity uart_tx is
  Generic(binary_flow: INTEGER := 300);
  Port (Clk          : in STD_LOGIC;
        Rst          : in STD_LOGIC;
        TxData       : in STD_LOGIC_VECTOR(15 downto 0);
        Tx           : out STD_LOGIC;
        RstTemp      : out STD_LOGIC);
end uart_tx;

architecture Behavioral of uart_tx is

signal frequency           : INTEGER := 100_000_000;
signal T_BIT               : INTEGER := frequency / binary_flow;
signal CntBit, CntRate     : INTEGER := 0;
signal LdData, ShData, TxEn: STD_LOGIC := '0';
signal TSR                 : STD_LOGIC_VECTOR(19 downto 0) := (others => '0');
type TYPE_STATE is (ready, load, send, waitbit, shift);
signal St                  : TYPE_STATE := ready;

begin

proc_control: process (Clk)
VARIABLE counter  : INTEGER RANGE 0 TO 999999999 := 0;
 begin
    if RISING_EDGE (Clk) then
        if (Rst = '1') then
            St <= ready;
        else
            case St is
                when ready =>
                    CntRate <= 0;
                    CntBit <= 0;
                    if (counter < 999999998) then
                        counter := counter + 1;
                        if (counter < 100000000) then 
                            RstTemp <= '1';
                        else
                            RstTemp <= '0';
                        end if;           
                    else                    
                        counter := 0;                     
                        St <= load;
                    end if;
                when load =>
                    St <= send;
                when send =>
                    CntBit <= CntBit + 1;
                    St <= waitbit;
                when waitbit =>
                    CntRate <= CntRate + 1;
                    if (CntRate = T_BIT - 3) then
                        CntRate <= 0;
                        St <= shift;
                    end if;
                when shift =>
                        if (CntBit = 20) then
                            St <= ready;
                        else
                            St <= send;
                        end if;
                when others =>
                    St <= ready;
            end case;
        end if;
    end if;
 end process proc_control;
 
TSR_proc: process(Clk, LdData, ShData)
 begin
    if RISING_EDGE(Clk) then
        if (Rst = '1') then
            TSR <= (others => '0');
        elsif (LdData = '1') then
            TSR <= '1' & TxData(15 downto 8) & '0' &'1' & TxData(7 downto 0) & '0';
        elsif (ShData = '1') then
            TSR <='0' & TSR(19 downto 1);
        else
            TSR <= TSR;
        end if;
    end if;
end process;
 
 LdData <= '1' when St = load else '0';
 ShData <= '1' when St = shift else '0';
 TxEn <= '0' when St = ready or St = load else '1';
 Tx <= TSR(0) when TxEn = '1' else '1';

end Behavioral;
