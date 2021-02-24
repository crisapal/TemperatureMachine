--- code adaptation after digillent's oficial website
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity pmod_temp_sensor IS
  generic(
    sys_clk_freq     : INTEGER := 50_000_000;              
    resolution       : INTEGER := 12;                    
    temp_sensor_addr : STD_LOGIC_VECTOR(6 downto 0) := "1001000"); 
  PORT(
    clk         : in    STD_LOGIC;                                 
    reset_n     : in    STD_LOGIC; 
    scl         : inout STD_LOGIC;                                 
    sda         : inout STD_LOGIC;                                
    i2c_ack_err : out   STD_LOGIC;                                
    temperature : out   STD_LOGIC_VECTOR(resolution-1 downto 0)); 
end pmod_temp_sensor;

architecture Behavioral of pmod_temp_sensor is
  type machine is(start, set_resolution, set_reg_pointer, read_data, output_result);
  signal state       : machine;                     
  signal config      : STD_LOGIC_VECTOR(7 downto 0);  
  signal i2c_ena     : STD_LOGIC;                    
  signal i2c_addr    : STD_LOGIC_VECTOR(6 downto 0); 
  signal i2c_rw      : STD_LOGIC;                     
  signal i2c_data_wr : STD_LOGIC_VECTOR(7 downto 0); 
  signal i2c_data_rd : STD_LOGIC_VECTOR(7 downto 0);  
  signal i2c_busy    : STD_LOGIC;                    
  signal busy_prev   : STD_LOGIC;                    
  signal temp_data   : STD_LOGIC_VECTOR(15 downto 0);

  COMPONENT i2c_master IS
    generic(
     input_clk : INTEGER;  
     bus_clk   : INTEGER);
    PORT(
     clk       : in     STD_LOGIC;                 
     reset_n   : in     STD_LOGIC;       
     ena       : in     STD_LOGIC;          
     addr      : in     STD_LOGIC_VECTOR(6 downto 0);
     rw        : in     STD_LOGIC;                 
     data_wr   : in     STD_LOGIC_VECTOR(7 downto 0);
     busy      : out    STD_LOGIC;                   
     data_rd   : out    STD_LOGIC_VECTOR(7 downto 0);
     ack_error : buffer STD_LOGIC;               
     sda       : inout  STD_LOGIC;               
     scl       : inout  STD_LOGIC);             
  end COMPONENT;

begin

  i2c_master_0:  i2c_master
    generic map(input_clk => sys_clk_freq, bus_clk => 400_000)
    port map(clk => clk, reset_n => reset_n, ena => i2c_ena, addr => i2c_addr,
             rw => i2c_rw, data_wr => i2c_data_wr, busy => i2c_busy,
             data_rd => i2c_data_rd, ack_error => i2c_ack_err, sda => sda,
             scl => scl);

  with resolution select
    config <= "00100000" when 10,  
              "01000000" when 11,  
              "01100000" when 12,   
              "00000000" when OTHERS; 

  process(clk, reset_n)
    variable busy_cnt : INTEGER range 0 to 2 := 0;            
    variable counter  : INTEGER range 0 to sys_clk_freq/10 := 0;
  begin
    if (reset_n = '0') then              
      counter := 0;                    
      i2c_ena <= '0';              
      busy_cnt := 0;                    
      --temperature <= (OTHERS => '0');     
      state <= start;    
    elsif RISING_EDGE (Clk) then 
      CASE state IS    
        when start =>
          if (counter < sys_clk_freq/10) then  
            counter := counter + 1;          
          else                           
            counter := 0;                   
            state <= set_resolution;          
          end if;
      
        when set_resolution =>            
          busy_prev <= i2c_busy;                     
          if (busy_prev = '0' and i2c_busy = '1') then
            busy_cnt := busy_cnt + 1;        
          end if;
          case busy_cnt IS                            
            when 0 =>                                
              i2c_ena <= '1';                           
              i2c_addr <= temp_sensor_addr;              
              i2c_rw <= '0';                       
              i2c_data_wr <= "00000001";           
            when 1 =>                            
              i2c_data_wr <= config;             
            when 2 =>                            
              i2c_ena <= '0';                    
              if(i2c_busy = '0') then            
                busy_cnt := 0;                   
                state <= set_reg_pointer;        
              end if;
            when OTHERS => NULL;
          end case;
          
        when set_reg_pointer =>
          busy_prev <= i2c_busy;                      
          if(busy_prev = '0' and i2c_busy = '1') then 
            busy_cnt := busy_cnt + 1;                 
          end if;
          case busy_cnt IS                            
            when 0 =>                                 
              i2c_ena <= '1';                         
              i2c_addr <= temp_sensor_addr;           
              i2c_rw <= '0';                          
              i2c_data_wr <= "00000000";              
            when 1 =>                                 
              i2c_ena <= '0';                         
              if(i2c_busy = '0') then                 
                busy_cnt := 0;                        
                state <= read_data;                   
              end if;
            when OTHERS => NULL;
          end case;
        
        when read_data =>
          busy_prev <= i2c_busy;                
          if(busy_prev = '0' and i2c_busy = '1') then  
            busy_cnt := busy_cnt + 1;                    
          end if;
          case busy_cnt IS                            
            when 0 =>                                
              i2c_ena <= '1';                        
              i2c_addr <= temp_sensor_addr;            
              i2c_rw <= '1';                        
            when 1 =>                            
              if(i2c_busy = '0') then                    
                temp_data(15 downto 8) <= i2c_data_rd; 
              end if;
            when 2 =>                                 
              i2c_ena <= '0';                       
              if(i2c_busy = '0') then                    
                temp_data(7 downto 0) <= i2c_data_rd; 
                busy_cnt := 0;                           
                state <= output_result;                   
              end if;
           when OTHERS => NULL;
          end case;

        when output_result =>
          temperature <= temp_data(15 downto 16-resolution); 
          state <= read_data;                           
          busy_cnt := 0;
        when OTHERS =>
          state <= start;

      end case;
    end if;
  end process; 
    
end Behavioral;
