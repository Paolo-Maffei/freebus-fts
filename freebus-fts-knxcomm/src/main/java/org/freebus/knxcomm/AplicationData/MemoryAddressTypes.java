package org.freebus.knxcomm.AplicationData;

public enum MemoryAddressTypes {
	/**
  *
  */
	AND_TAB("AND_TAB"),

	/**
   *
   */
	ApplicationID("ApplicationID"),

	/**
   *
   */
	AssocTabPtr("AssocTabPtr"),

	/**
   *
   */
	BootloaderROM("BootloaderROM"),

	/**
   *
   */
	Bootloadervectors("Bootloadervectors"),

	/**
   *
   */
	CheckLim("CheckLim"),

	/**
   *
   */
	CommsTabPtr("CommsTabPtr"),

	/**
   *
   */
	ConfigDes("ConfigDes"),

	/**
    *
    */
	I_O_space1("I_O_space1"),

	/**
 *
 */
	I_O_space2("I_O_space2"),

	/**
   *
   */
	IregB_N("IregB_N"),

	/**
   *
   */
	ManData("ManData"),

	/**
   *
   */
	Masktype("Masktype"),

	/**
   *
   */
	maskversion("maskversion"),

	/**
   *
   */
	MS_Buffer("MS_Buffer"),

	/**
   *
   */
	MxRstCnt("MxRstCnt"),

	/**
   *
   */
	OptionReg("OptionReg"),

	/**
   *
   */
	OR_TAB("OR_TAB"),

	/**
*
*/
	Page0ROM("Page0ROM"),

	/**
   *
   */
	PEI_Buff("PEI_Buff"),

	/**
   *
   */
	PEI_Info("PEI_Info"),

	/**
   *
   */
	PEI_Interface("PEI_Interface"),

	/**
   *
   */
	PEI_RecBuf("PEI_RecBuf"),

	/**
   *
   */
	PEI_SndBuf("PEI_SndBuf"),

	/**
   *
   */
	PEI_Type("PEI_Type"),

	/**
   *
   */
	PortADDR("PortADDR"),

	/**
   *
   */
	PortCDDR("PortCDDR"),

	/**
   *
   */
	protectedEEPROM("protectedEEPROM"),

	/**
   *
   */
	RegB_N("RegB_N"),

	/**
   *
   */
	RouteCnt("RouteCnt"),

	/**
   *
   */
	RunError("RunError"),

	/**
   *
   */
	stack("stack"),

	/**
   *
   */
	SyncRate("SyncRate"),

	/**
   *
   */
	System("System"),

	/**
   *
   */
	system1("system1"),

	/**
   *
   */
	system2("system2"),

	/**
   *
   */
	SystemROM("SystemROM"),

	/**
   *
   */
	SystemROMvectors(""),

	/**
   *
   */
	SystemState("SystemState"),

	/**
   *
   */
	UserRAM("UserRAM"),

	/**
   *
   */
	userRAM2("userRAM2"),

	/**
   *
   */
	UsrEEPROM("UsrEEPROM"),

	/**
   *
   */
	UsrInitPtr("UsrInitPtr"),

	/**
   *
   */
	UsrPrgPtr("UsrPrgPtr"),

	/**
   *
   */
	UsrSavPtr("UsrSavPtr"),

	;

	public final String function;

	/**
	 * Internal constructor
	 */
	private MemoryAddressTypes(String function) {
		this.function = function;

	}

	/**
	 * @return the application type in human readable form.
	 */
	public String getfunction() {
		return function;
	}

}
