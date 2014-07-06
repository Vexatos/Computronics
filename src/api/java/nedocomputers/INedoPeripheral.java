package nedocomputers;

public interface INedoPeripheral {
	boolean Connectable(int side);
	short busRead(int addr);
	void busWrite(int addr, short data);
	int getBusId();
	void setBusId(int id);
}
