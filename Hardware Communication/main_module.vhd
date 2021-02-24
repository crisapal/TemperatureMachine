library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity main_module is
  Port (Clk        : in STD_LOGIC;
        Rst        : in STD_LOGIC;
        Scl        : inout STD_LOGIC;
        Sda        : inout STD_LOGIC;
        i2c_ack_err: out STD_LOGIC;
        Tx         : out STD_LOGIC);
end main_module;

architecture Behavioral of main_module is

signal temperature: STD_LOGIC_VECTOR(11 downto 0) := (others => '0');
signal TxData     : STD_LOGIC_VECTOR(15 downto 0) := (others => '0');
signal RstTemp    : STD_LOGIC := '0';

begin

TxData <= "0000" & temperature;
                                            
portPmod: entity work.pmod_temp_sensor port map(clk => Clk,
                                        reset_n => RstTemp,
                                        scl => scl,
                                        sda => sda,
                                        i2c_ack_err => i2c_ack_err,
                                        temperature => temperature);
                                            
portUART: entity work.uart_tx generic map(binary_flow => 300)
                              port map(Clk => Clk,
                                       Rst => Rst,
                                       TxData => TxData,
                                       Tx => Tx,
                                       RstTemp => RstTemp);

end Behavioral;
