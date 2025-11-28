package vag2egl;

/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
//---------------------------------------------------------
// HN01 2007-03-01 : Split map
// HN02 2007-03-06 : Correct mapg
// HN03 2007-03-12 : Merge map
// MH01 2009-07-12 : Generalize for more customers 
//---------------------------------------------------------
import java.io.*;
import java.util.*;
import java.lang.*;

public class Vag2Egl2 {

	static String inpPrefix = null;
	static String rec1 = null;

	static PrintStream fpAppl = null;
	static PrintStream fpCm = null;
	static PrintStream fpCmMerge = null;
	static PrintStream fpMap = null;
	static PrintStream fpMapG = null;
	static String inpFileId = null;
	static String inpCustomer = null; // MH01 - FMS/ATP/DSV/ADG/KKI
	static String inpPartName = null; // MH01 - contains the name of the VAG
										// part
	static String apNewFileId = null;
	static String apOldFileId = null;
	static String cmNewFileId = null;
	static String cmOldFileId = null;
	static String cmTabFileId[] = new String[100000];
	static int cmTabFileIx = -1;
	static String apTabFileId[] = new String[100000];
	static int apTabFileIx = -1;
	static String mgNewFileId = null;
	static String mgOldFileId = null;
	static String mgTabFileId[] = new String[100000];
	static int mgTabFileIx = -1;
	static String apEnd = "";
	static String cmFileId = null;
	static String cmEnd = null;
	static char cmFunc = ' ';
	static String mapId = null;
	static String mapEnd = null;
	static char mapFunc = ' ';
	static int recCnt = 0;
	static int recIdx = 0;
	static String pathName = "";
	static boolean bypass = false; // HN03

	// -------------------------------------------------------
	void EditCommonFileId(String inpRec) {

		System.out.println("   ------------ start EditCommonFileId"); // TEST
		System.out.println("      --------- cmFileId: " + cmFileId); // TEST

		if (inpRec.startsWith(":program ") && inpRec.length() > 23) {
			cmFileId = inpRec.substring(23, inpRec.length());
			cmFunc = 'O';
			cmEnd = ":eprogram.";
			return;
		}

		if (inpRec.startsWith(":func ") && inpRec.length() > 23) {
			cmFileId = inpRec.substring(23, inpRec.length());
			cmFunc = 'O';
			cmEnd = ":efunc.";
			return;
		}

		if (inpRec.startsWith(":record ") && inpRec.length() > 23) {
			cmFileId = inpRec.substring(23, inpRec.length());
			cmFunc = 'O';
			cmEnd = ":erecord.";
			return;
		}

		if (inpRec.startsWith(":item ") && inpRec.length() > 23) {
			cmFileId = inpRec.substring(23, inpRec.length());
			cmFunc = 'O';
			cmEnd = ":eitem.";
			return;
		}

		/*
		 * if (inpRec.startsWith(":map ") && inpRec.length() > 46) { cmFileId =
		 * inpRec.substring(46, inpRec.length()); cmFunc = 'O'; cmEnd =
		 * ":emap."; return; } if (inpRec.startsWith(":map ") && inpRec.length()
		 * > 26) { if (inpRec.length() <= 27) { cmFileId = inpRec.substring(23,
		 * inpRec.length()-1); } else { cmFileId = inpRec.substring(23, 27) +
		 * "A"; } cmFunc = 'O'; cmEnd = ":emap."; return; }
		 */
		// HN03
		if (inpRec.startsWith(":map ") && inpRec.length() > 49) { // map name
			// mapId = inpRec.substring(46, inpRec.length());
			int idx = inpRec.substring(23, inpRec.length()).indexOf(' ') + 23;
			// String wMap = inpRec.substring(46, inpRec.length()); // HN03
			// String wGrp = inpRec.substring(23, idx); //HN03
			String wMap = inpRec.substring(46, 50); // HN03
			String wGrp = inpRec.substring(23, idx - 1); // HN03
			bypass = false;
			if (!wGrp.equals(wMap)) {
				bypass = true;
			}
			// System.out.println("grp=" + wGrp + " map=" + wMap + " bypass=" +
			// bypass);
			// System.out.println("?: " + inpRec.substring(23, idx) + " rec=" +
			// inpRec );
			cmFileId = inpRec.substring(23, idx);
			cmFunc = 'O';
			cmEnd = ":emap.";
			return;
		}
		// HN01
		if (inpRec.startsWith(":map ") && inpRec.length() > 46) {
			int idx = inpRec.substring(23, inpRec.length()).indexOf(' ') + 23;
			cmFileId = inpRec.substring(23, idx) + "-"
					+ inpRec.substring(46, inpRec.length());
			cmFunc = 'O';
			cmEnd = ":emap.";
			return;
		}

	}

	// -------------------------------------------------------
	boolean CheckCommonMerge(String inpRec) {

		System.out.println("checkCommonMerge - begin:" + inpRec); // TEST

		if (inpFileId.equals("ML")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 2).startsWith("FC"))) {
					// HN03 cmNewFileId = cmFileId.substring(0, 4) + "A";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "PROC";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "A";
					}
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("F5JC"))
						|| (cmFileId.substring(0, 4).startsWith("F901"))
						|| (cmFileId.substring(0, 4).startsWith("F902"))
						|| (cmFileId.substring(0, 4).startsWith("F905"))
						|| (cmFileId.substring(0, 4).startsWith("F906"))
						|| (cmFileId.substring(0, 4).startsWith("F907"))
						|| (cmFileId.substring(0, 4).startsWith("F908"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "PROC";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					}
					return true;
				}
			}


			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("F903"))
						|| (cmFileId.substring(0, 4).startsWith("F904"))
						|| (cmFileId.substring(0, 4).startsWith("F910"))
						|| (cmFileId.substring(0, 4).startsWith("F912"))
						|| (cmFileId.substring(0, 3).startsWith("F94"))
						|| (cmFileId.substring(0, 3).startsWith("F95"))
						|| (cmFileId.substring(0, 4).startsWith("F969"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("GEN")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("G901"))
						|| (cmFileId.substring(0, 4).startsWith("G902"))
						|| (cmFileId.substring(0, 4).startsWith("G903"))
						|| (cmFileId.substring(0, 4).startsWith("G904"))
						|| (cmFileId.substring(0, 4).startsWith("G905"))
						|| (cmFileId.substring(0, 4).startsWith("G906"))
						|| (cmFileId.substring(0, 4).startsWith("G907"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "PROC";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					}
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 3).startsWith("G94"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("DEB")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("D901"))
						|| (cmFileId.substring(0, 4).startsWith("D902"))
						|| (cmFileId.substring(0, 4).startsWith("D908"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "PROC";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					}
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("D922"))
						|| (cmFileId.substring(0, 4).startsWith("D925"))
						|| (cmFileId.substring(0, 4).startsWith("D940"))
						|| (cmFileId.substring(0, 4).startsWith("D943"))
						|| (cmFileId.substring(0, 4).startsWith("D999"))) {
					cmNewFileId = cmFileId.substring(0, 4) + "A";
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("D941"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("INV")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("I900"))
						|| (cmFileId.substring(0, 4).startsWith("I901"))
						|| (cmFileId.substring(0, 4).startsWith("I902"))) {
					cmNewFileId = cmFileId.substring(0, 4) + "A";
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("I941"))
						|| (cmFileId.substring(0, 4).startsWith("I942"))
						|| (cmFileId.substring(0, 4).startsWith("I943"))
						|| (cmFileId.substring(0, 4).startsWith("I944"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("KRE")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("K901"))
						|| (cmFileId.substring(0, 4).startsWith("K902"))
						|| (cmFileId.substring(0, 4).startsWith("K908"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "PROC";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "PROC";
					}
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("K921"))
						|| (cmFileId.substring(0, 4).startsWith("K922"))
						|| (cmFileId.substring(0, 4).startsWith("K923"))
						|| (cmFileId.substring(0, 4).startsWith("K925"))
						|| (cmFileId.substring(0, 4).startsWith("K980"))
						|| (cmFileId.substring(0, 4).startsWith("K981"))
						|| (cmFileId.substring(0, 4).startsWith("K999"))) {
					cmNewFileId = cmFileId.substring(0, 4) + "A";
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("K941"))
						|| (cmFileId.substring(0, 4).startsWith("K943"))
						|| (cmFileId.substring(0, 4).startsWith("K944"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("FAA")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("A900"))
						|| (cmFileId.substring(0, 4).startsWith("A901"))
						|| (cmFileId.substring(0, 4).startsWith("A902"))
						|| (cmFileId.substring(0, 4).startsWith("A903"))) {
					cmNewFileId = cmFileId.substring(0, 4) + "A";
					return true;
				}
			}

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 4).startsWith("A940"))
						|| (cmFileId.substring(0, 4).startsWith("A941"))
						|| (cmFileId.substring(0, 4).startsWith("A942"))
						|| (cmFileId.substring(0, 4).startsWith("A943"))
						|| (cmFileId.substring(0, 4).startsWith("A944"))
						|| (cmFileId.substring(0, 4).startsWith("A945"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpFileId.equals("CLI")) {

			if (cmFileId != null && cmFileId.length() > 3) {
				if ((cmFileId.substring(0, 3).startsWith("L94"))) {
					// HN01 cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId + "POPUP";
					} else {
						cmNewFileId = cmFileId.substring(0, 4) + "POPUP";
					}
					return true;
				}
			}

		}

		if (inpCustomer.equals("ATP")) {
			// System.out.println("hey checkCommonMerge - ATP. cmFileId:" + cmFileId + " length:" + cmFileId.length()+ ":" + cmFileId.charAt(2) + ":" + cmFileId.charAt(3));			
			if (cmFileId != null && cmFileId.length() > 3) {
				if (cmFileId.startsWith(inpPrefix) && cmFileId.charAt(2) == 'X' && cmFileId.charAt(3) == 'X')
				{
					// System.out.println("hey hey checkCommonMerge - ATP. cmFileId:" + cmFileId);			
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId;
					} else {
						cmNewFileId = cmFileId.substring(0, 4);
					}
					return true;
				}
			}
		}

		if (inpCustomer.equals("DSV")) {
			// System.out.println("hey checkCommonMerge - DSV. cmFileId:" + cmFileId + " length:" + cmFileId.length()+ ":" + cmFileId.charAt(2) + ":" + cmFileId.charAt(3));			
			if (cmFileId != null && cmFileId.length() > 3) {
				if (cmFileId.startsWith(inpPrefix) && cmFileId.charAt(2) == 'X' && cmFileId.charAt(3) == 'X')
				{
					// System.out.println("hey hey checkCommonMerge - DSV. cmFileId:" + cmFileId);			
					if (cmEnd.equals(":emap.")) {
						cmNewFileId = cmFileId;
					} else {
						cmNewFileId = cmFileId.substring(0, 4);
					}
					return true;
				}
			}
		}
		
		return false;

	}

	// -------------------------------------------------------
	boolean CheckCommon(String inpRec) {

		// if (inpCustomer.equals("KKI"))
		// {
		// //test System.out.println("checkCommon - KKI"); //TEST
		// if (inpRec.startsWith(":program ") ||
		// inpRec.startsWith(":func ") ||
		// inpRec.startsWith(":record ") ||
		// inpRec.startsWith(":item ") ) {
		// inpPartName= inpRec.substring(23, inpRec.length()); //MH01
		// System.out.println("checkCommon - inpPartName:" + inpPartName);
		// //TEST
		// //KK if (inpRec.length() > 23 && !inpRec.substring(23,
		// 24).equals(inpPrefix) ) {
		// //test if (inpRec.length() > 23 )
		// //test {
		// //test System.out.println("inpRecstartsWith" + inpRec.substring(0, 8)
		// + "inpRec:" + inpRec.substring(23, inpRec.length()) + ":inpPrefix=" +
		// inpPrefix);
		// System.out.println("inpRecstartsWith: " + inpRec.substring(0, 8) +
		// " inpPartName:" + inpPartName + " :inpPrefix=" + inpPrefix);
		// //test }
		// //KK if (inpRec.length() > 23 && !inpRec.substring(23,
		// 26).equals(inpPrefix) ) {
		// //KK if (inpRec.length() > 23 )
		// //test if (inpRec.length() > 26 && !inpRec.substring(23,
		// 26).equals(inpPrefix) ) //KK
		// if (inpRec.length() > 26 && !inpPartName.startsWith(inpPrefix) ) //KK
		// {
		// System.out.println(">26:" + inpRec.substring(26, 27)); // KKtest
		// //test if (!inpRec.substring(23, 26).equals(inpPrefix) ||
		// inpRec.length() > 27 && inpRec.substring(26, 27).equals("-") )
		// if (!inpPartName.startsWith(inpPrefix) || inpRec.length() > 27 &&
		// !inpRec.substring(26, 27).equals("-") )
		// {
		// System.out.println("EditCommonFileId - inpPartName:" + inpPartName +
		// " substr:" + inpRec.substring(26, 27) );
		// EditCommonFileId(inpRec);
		// return true;
		// }
		// }
		// }
		// } // inpCustomer.equals("KKI")

		System.out.println("checkCommon - inpRec:" + inpRec); // TEST

		if (inpCustomer.equals("ATP")) {
			System.out.println("checkCommon - ATP"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - ATP. inpPartName="
						+ inpPartName + " inpPrefix:" + inpPrefix + " inpRec.length:" + inpRec.length()); // TEST
				if (inpPartName.startsWith(inpPrefix)	&& inpRec.length() > 26 && inpPartName.charAt(2) != 'X' && inpPartName.charAt(3) != 'X') {
					System.out.println("checkCommon - ATP. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					System.out
							.println("--------------- before EditCommonFileId checkCommon - ATP."); // TEST
					EditCommonFileId(inpRec);
					System.out
							.println("--------------- after  EditCommonFileId checkCommon - ATP."); // TEST
					System.out.println("--------------- cmFileId: " + cmFileId); // TEST
					return true;
				}
			}
		} // inpCustomer.equals("ATP")

		if (inpCustomer.equals("DSV")) {
			System.out.println("checkCommon - DSV"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - ATP. inpPartName="
						+ inpPartName + " inpPrefix:" + inpPrefix + " inpRec.length:" + inpRec.length()); // TEST
				if (inpPartName.startsWith(inpPrefix)	&& inpRec.length() > 26 && inpPartName.charAt(2) != 'X' && inpPartName.charAt(3) != 'X') {
					System.out.println("checkCommon - DSV. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					System.out
							.println("--------------- before EditCommonFileId checkCommon - DSV."); // TEST
					EditCommonFileId(inpRec);
					System.out
							.println("--------------- after  EditCommonFileId checkCommon - DSV."); // TEST
					System.out.println("--------------- cmFileId: " + cmFileId); // TEST
					return true;
				}
			}
		} // inpCustomer.equals("DSV")

		if (inpCustomer.equals("KKI")) {
			System.out.println("checkCommon - KKI"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - KKI. inpPartName="
						+ inpPartName); // TEST
				if ((inpPartName.startsWith(inpPrefix))
						&& Character.isDigit(inpPartName.charAt(3))
						&& Character.isDigit(inpPartName.charAt(4))) {
					System.out.println("checkCommon - KKI. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					System.out
							.println("--------------- before EditCommonFileId checkCommon - KKI."); // TEST
					EditCommonFileId(inpRec);
					System.out
							.println("--------------- after  EditCommonFileId checkCommon - KKI."); // TEST
					System.out.println("--------------- cmFileId: " + cmFileId); // TEST
					return true;
				}
			}
		} // inpCustomer.equals("KKI")

		if (inpCustomer.equals("SMC")) {
			System.out.println("checkCommon - SMC"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - SMC. inpPartName="
						+ inpPartName); // TEST
				if ((inpPartName.startsWith("A") || inpPartName.startsWith("D")
						|| inpPartName.startsWith("F") || inpPartName
						.startsWith("K"))
						&& Character.isDigit(inpPartName.charAt(1))
						&& Character.isDigit(inpPartName.charAt(2))
						&& Character.isDigit(inpPartName.charAt(3))
						|| inpPartName.startsWith("FC60")) {
					System.out.println("checkCommon - SMC. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					System.out
							.println("--------------- before EditCommonFileId checkCommon - SMC."); // TEST
					EditCommonFileId(inpRec);
					System.out
							.println("--------------- after  EditCommonFileId checkCommon - SMC."); // TEST
					System.out.println("--------------- cmFileId: " + cmFileId); // TEST
					return true;
				}
			}
		} // inpCustomer.equals("SMC")

		if (inpCustomer.equals("NYK")) {
			System.out.println("checkCommon - NYK"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - NYK. inpPartName="
						+ inpPartName); // TEST
				if ((inpPartName.startsWith("F") || inpPartName.startsWith("K") || inpPartName
						.startsWith("U"))
						&& Character.isDigit(inpPartName.charAt(1))
						&& Character.isDigit(inpPartName.charAt(2))
						&& Character.isDigit(inpPartName.charAt(3))
						|| inpPartName.startsWith("FC60")) {
					System.out.println("checkCommon - NYK. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					EditCommonFileId(inpRec);
					return true;
				}
			}
		} // inpCustomer.equals("NYK")

		if (inpCustomer.equals("BA")) {
			System.out.println("checkCommon - BA"); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				inpPartName = inpRec.substring(23, inpRec.length());
				System.out.println("checkCommon - BA. inpPartName="
						+ inpPartName); // TEST
				if ((inpPartName.startsWith("C") || inpPartName.startsWith("D")
						|| inpPartName.startsWith("F")
						|| inpPartName.startsWith("G")
						|| inpPartName.startsWith("K") || inpPartName
						.startsWith("L"))
						&& Character.isDigit(inpPartName.charAt(1))
						&& Character.isDigit(inpPartName.charAt(2))
						&& Character.isDigit(inpPartName.charAt(3))
						|| inpPartName.startsWith("FC60")) {
					System.out.println("checkCommon - BA. inpPartName:"
							+ inpPartName + " - dont select"); // TEST
				} else {
					EditCommonFileId(inpRec);
					return true;
				}
			}
		} // inpCustomer.equals("BA")

		if (inpCustomer.equals("FMS")) {
			System.out.println("checkCommon - FMS. inpRec=" + inpRec); // TEST
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				// KK if (inpRec.length() > 23 && !inpRec.substring(23,
				// 24).equals(inpPrefix) ) {
				if (inpRec.length() > 23) {
					System.out.println("inpRecstartsWith"
							+ inpRec.substring(0, 8) + "inpRec:"
							+ inpRec.substring(23, inpRec.length())
							+ ":inpPrefix=" + inpPrefix);
				}
				// KK if (inpRec.length() > 23 && !inpRec.substring(23,
				// 26).equals(inpPrefix) ) {
				// KK if (inpRec.length() > 23 )
				if (inpRec.length() > 26
						&& !inpRec.substring(23, 26).equals(inpPrefix)) {
					// KKtest System.out.println(">26:"); // KKtest
					if (!inpRec.substring(23, 26).equals(inpPrefix)
							|| inpRec.length() > 27
							&& inpRec.substring(26, 27).equals("-")) {
						System.out.println("EditCommonFileId");
						EditCommonFileId(inpRec);
						return true;
					}
				}
			}

			if (inpFileId.equals("ML") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 24 && inpRec.substring(23, 25).equals(
						"FL"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("FO"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("FP"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 26)
								.equals("FIN"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 26)
								.equals("FDS"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 26)
								.equals("FMS"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("FF10"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("FKEY"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 28)
								.equals("FIERR"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 28)
								.equals("FIPAY"))
						|| (inpRec.length() > 29 && inpRec.substring(23, 30)
								.equals("FJFOUND"))
						|| (inpRec.length() > 30 && inpRec.substring(23, 31)
								.equals("FJNXTTYP"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("FELT"))) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			if (inpFileId.equals("GEN") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 24
						&& inpRec.substring(23, 24).equals("G") && !inpRec
						.substring(23, 25).equals("G0"))) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			// KK if (inpFileId.equals("DEB") && inpRec.startsWith(":item ")) {
			if (inpFileId.equals("XXX") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 24 && inpRec.substring(23, 25).equals(
						"DA"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DB"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DF"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DG"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DI"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DL"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DM"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DN"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DP"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DS"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("DT"))) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			if (inpFileId.equals("INV") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 24 && inpRec.substring(23, 25).equals(
						"IC"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("ID"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IG"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IH"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IL"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IP"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IT"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IV"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("IX"))) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			if (inpFileId.equals("KRE") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 24 && inpRec.substring(23, 25).equals(
						"KA"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KD"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KG"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KI"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KL"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KN"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KP"))
						|| (inpRec.length() > 24 && inpRec.substring(23, 25)
								.equals("KS"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K12A"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K12B"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K19A"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K102"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K200"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 27)
								.equals("K203"))

				) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			if (inpFileId.equals("FAA")) {

				if (inpRec.startsWith(":func ")) {

					if ((inpRec.length() > 29 && inpRec.substring(23, 30)
							.equals("A21DB_P"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A500PIQ"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A50DB_P"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A51DB_P"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A52DB_P"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A60DB_P"))
							|| (inpRec.length() > 31 && inpRec
									.substring(23, 32).equals("A60DB_ADD"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("A70DB_P"))) {
						EditCommonFileId(inpRec);
						return true;
					}

				}

				if (inpRec.startsWith(":item ") && inpRec.length() > 24) {
					if (inpRec.substring(23, 24).equals("A")
							&& inpRec.substring(24, 25).compareTo("9") > 0) {
						EditCommonFileId(inpRec);
						return true;
					}
				}
			}

			if (inpFileId.equals("CLI") && inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 26 && inpRec.substring(23, 26).equals(
						"L90"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 26)
								.equals("L94"))
						|| (inpRec.length() > 29 && inpRec.substring(23, 30)
								.equals("L105PGM"))) {
					EditCommonFileId(inpRec);
					return true;
				}

			}

			if (inpRec.startsWith(":func ") && inpRec.length() > 26) {
				if ((inpRec.substring(23, 24).startsWith("G") && !inpRec
						.substring(23, 25).startsWith("G0"))
						|| (inpRec.substring(23, 24).startsWith("K") && !inpRec
								.substring(23, 25).startsWith("K0"))
						|| (inpRec.substring(23, 24).startsWith("D") && !inpRec
								.substring(23, 25).startsWith("D0"))
						|| (inpRec.substring(23, 24).startsWith("L")
								&& !inpRec.substring(23, 25).startsWith("L0")
								&& !inpRec.substring(23, 26).startsWith("L11") && !inpRec
								.substring(23, 26).startsWith("L18"))) {
					EditCommonFileId(inpRec);
					return true;
				}
			}

			if (!inpFileId.equals("INV")) {
				if (inpRec.startsWith(":record ")) {
					if (inpRec.endsWith("FDB") || inpRec.endsWith("FDB1")
							|| inpRec.endsWith("FDB2")) {
						EditCommonFileId(inpRec);
						return true;
					}
				}
			}

			if (inpFileId.equals("INV")) {
				if (inpRec.startsWith(":record ")) {
					if ((inpRec.length() > 29 && inpRec.substring(23, 30)
							.equals("I9ILFDB"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("I9HLFDB"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("I9IVFDB"))
							|| (inpRec.length() > 29 && inpRec
									.substring(23, 30).equals("I9HVFDB"))) {

						EditCommonFileId(inpRec);
						return true;
					}
				}
			}

			if (inpRec.startsWith(":record ") && inpRec.length() > 26) {
				if ((inpRec.substring(23, 24).startsWith("G") && !inpRec
						.substring(23, 25).startsWith("G0"))
						|| (inpRec.substring(23, 24).startsWith("K") && !inpRec
								.substring(23, 25).startsWith("K0"))
						|| (inpRec.substring(23, 24).startsWith("D") && !inpRec
								.substring(23, 25).startsWith("D0"))
						|| (inpRec.substring(23, 24).startsWith("L")
								&& !inpRec.substring(23, 25).startsWith("L0")
								&& !inpRec.substring(23, 26).startsWith("L11") && !inpRec
								.substring(23, 26).startsWith("L18"))) {
					EditCommonFileId(inpRec);
					return true;
				}
			}

			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {

				if ((inpRec.length() > 27 && inpRec.substring(23, 27)
						.startsWith("COPY"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("FC"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("FG"))
						|| (inpRec.length() > 26 && inpRec.substring(23, 26)
								.startsWith("F5J"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 27)
								.startsWith("F52X"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 27)
								.startsWith("F53X"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 27)
								.startsWith("F11X"))
						||
						// (inpRec.length() > 27 && inpRec.substring(23,
						// 28).startsWith("F11XP")) ||
						// (inpRec.length() > 27 && inpRec.substring(23,
						// 28).startsWith("F11XS")) ||
						// (inpRec.length() > 27 && inpRec.substring(23,
						// 28).startsWith("F11XV")) ||
						(inpRec.length() > 28 && inpRec.substring(23, 29)
								.startsWith("ZF185G"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 28)
								.startsWith("FF10W"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 28)
								.startsWith("FMSIF"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 27)
								.startsWith("FJMP"))
						|| (inpRec.length() > 27 && inpRec.substring(23, 27)
								.startsWith("FFDS"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("F9"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("G9"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("GG"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("K9"))
						|| (inpRec.length() > 29 && inpRec.substring(23, 30)
								.startsWith("K03XWUS"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("D9"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("I9"))
						|| (inpRec.length() > 25 && inpRec.substring(23, 25)
								.startsWith("A9"))
						|| (inpRec.length() > 28 && inpRec.substring(23, 29)
								.startsWith("A50FDB"))
						|| (inpRec.length() > 29 && inpRec.substring(23, 30)
								.startsWith("K03XWUS"))
						|| (inpRec.length() > 30 && inpRec.substring(23, 30)
								.startsWith("F-CALL-"))) {
					EditCommonFileId(inpRec);
					return true;
				}
			}
		} // inpCustomer("FMS")

		if (cmEnd != null) {
			System.out.println("checkCommon - cmEnd:" + cmEnd); // TEST

			if (inpRec.startsWith(cmEnd)) {
				cmFunc = 'C';
				System.out
						.println("checkCommon - cmEnd - Close ======================"); // TEST
			} else {
				cmFunc = 'W';
			}
			return true;
		}

		return false;
	}

	// -------------------------------------------------------
	boolean CheckMap(String inpRec) {

		if (inpRec.startsWith(":map ") && inpRec.length() > 46) { // map name
			mapId = inpRec.substring(46, inpRec.length());
			int idx = inpRec.substring(23, inpRec.length()).indexOf(' ') + 23;
			// System.out.println("?: " + inpRec.substring(23, idx) + " rec=" +
			// inpRec );
			mgNewFileId = inpRec.substring(23, idx);
			mapFunc = 'O';
			mapEnd = ":emap.";
			return true;
		}

		// HN02
		if (inpRec.startsWith(":mapg ") && inpRec.length() > 23) { // mapg name
			mgNewFileId = inpRec.substring(23, inpRec.length());
			// System.out.println("mapg?: " + mgNewFileId + " rec=" + inpRec );
			mapFunc = 'O';
			mapEnd = ":emapg.";
			return true;
		}

		if (mapEnd != null) {
			if (inpRec.startsWith(mapEnd)) {
				mapFunc = 'C';
			} else {
				mapFunc = 'W';
			}
			return true;
		}

		return false;
	}

	// -----------------------------------------------------------
	boolean checkPath(String pathName) {

		boolean exists = (new File(pathName)).exists();
		if (exists)
			return true;

		StringTokenizer dir = new StringTokenizer(pathName, "/");

		String path = "";
		int idx = 0;

		while (dir.hasMoreTokens()) {
			idx++;
			if (idx == 1) {
				path = dir.nextToken();
			} else {
				path += "/" + dir.nextToken();
			}
			exists = (new File(path)).exists();
			if (!exists) {
				// Create a directory; all non-existent ancestor directories are
				// automatically created
				boolean success = (new File(path)).mkdirs();
				if (!success) {
					System.out.println("Error create directory " + path);
					return false;
				} else {
					System.out.println("Create directory " + path);
				}
			}
		}

		return true;

	}

	// -------------------------------------------------------
	void WriteCommonFile(String inpRec) {

		System.out.println("gotthere");
		if (CheckCommonMerge(inpRec)) {
			System.out.println("gotthere2");
			WriteCommonMergeFile(inpRec);
			return;
		}

		try {

			if (cmFunc == 'O') {
				// System.out.println("heyda WriteCommonFile cmFileId:" + cmFileId + " length:" + cmFileId.length() + "cmEnd:" + cmEnd);			

				if (cmEnd.equals(":eitem.") || cmEnd.equals(":erecord.")) {
					// System.out.println("cmEnd=" + cmEnd + " ?=" +
					// cmEnd.substring(2, cmEnd.length()-1));
					pathName = inpFileId + "/cm/"
							+ cmEnd.substring(2, cmEnd.length() - 1);
					if (checkPath(pathName)) {
						fpCm = new PrintStream(new FileOutputStream(inpFileId
								+ "/cm/"
								+ cmEnd.substring(2, cmEnd.length() - 1) + "/"
								+ cmFileId + ".esf"));
					} else {
						System.exit(0);
					}
				} else {
					pathName = inpFileId + "/cm";
					if (checkPath(pathName)) {
						fpCm = new PrintStream(new FileOutputStream(inpFileId
								+ "/cm/" + cmFileId + ".esf"));
					} else {
						System.exit(0);
					}
				}
				fpCm.println(rec1);
				fpCm.println(inpRec);
			} else {
				if (cmFunc == 'W') {
					fpCm.println(inpRec);
				} else {
					if (cmFunc == 'C') {
						fpCm.println(inpRec);
						fpCm.close();
						cmFunc = ' ';
						cmEnd = null;
					}
				}
			}

		}

		catch (FileNotFoundException e) {
			System.out.println("IO error: " + e);
			System.out.println("Common fileID=" + cmFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.out.println("Common fileID=" + cmFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		}

	}

	// -------------------------------------------------------
	void WriteCommonMergeFile(String inpRec) {

		if (cmEnd.equals(":emap.") && bypass)
			return;

		/*
		 * HN03 // Bypass if mapname not = mapgrp HN01 if
		 * (cmEnd.equals(":emap.")) { int idx = cmNewFileId.indexOf('-'); if
		 * (idx >=0) { String prefix1 = cmNewFileId.substring(0, 4); String
		 * prefix2 = cmNewFileId.substring(idx+1, idx+5); if
		 * (!prefix1.equals(prefix2)) { //System.out.println("idx=" + idx +
		 * " 1=" + prefix1 + " 2=" + prefix2 + " s=" + cmNewFileId); return; } }
		 * }
		 */

		try {

			if (cmFunc == 'O') {
				//System.out.println("davdav cmNewFileID=" + cmNewFileId + " Old fileID=" + cmOldFileId);
				if (!cmNewFileId.equals(cmOldFileId)) {
					cmOldFileId = cmNewFileId;
					if (fpCmMerge != null)
						fpCmMerge.close();
				}
				// HN01
				if (cmNewFileId.endsWith("POPUP"))
					pathName = inpFileId + "/cm/popup";
				else
					pathName = inpFileId + "/cm";

				//System.out.println("davdav2 pathName=" + pathName);

				
				String wFile = cmNewFileId;
				if (cmEnd.equals(":emap.")) {
					if (cmNewFileId.endsWith("POPUP"))
						wFile = cmNewFileId.substring(0,
								cmNewFileId.length() - 5);
					if (cmNewFileId.endsWith("PROC"))
						wFile = cmNewFileId.substring(0,
								cmNewFileId.length() - 4);
					// System.out.println("HN03 wFile=" + wFile + " Org=" +
					// cmNewFileId);
				}
				if (checkPath(pathName)) {
					fpCmMerge = new PrintStream(new FileOutputStream(pathName
							+ "/" + wFile + ".esf", true));
				} else {
					System.exit(0);
				}

				boolean fnd = false;
				// System.out.println("Arr notfnd ix=" + cmTabFileIx + " lng=" +
				// cmTabFileId.length);
				if (cmTabFileIx >= 0) {
					for (int i = 0; i <= cmTabFileIx; i++) {
						if (!cmTabFileId[i].equals("")
								&& cmTabFileId[i].equals(cmNewFileId)) {
							fnd = true;
							break;
						}
					}
				}

				if (!fnd && cmNewFileId != null) {
					cmTabFileIx += 1;
					// System.out.println("Arr notfnd ix=" + cmTabFileIx +
					// " file=" + cmNewFileId);
					cmTabFileId[cmTabFileIx] = cmNewFileId;
					fpCmMerge.println(rec1);
				}

				fpCmMerge.println(inpRec);

			} else {
				if (cmFunc == 'W') {
					fpCmMerge.println(inpRec);

				} else {
					if (cmFunc == 'C') {
						fpCmMerge.println(inpRec);
						fpCmMerge.close();
						cmFunc = ' ';
						cmEnd = null;
					}
				}
			}

		}

		catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
			System.out.println("FileID=" + cmNewFileId + " Old fileID="
					+ cmOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.out.println("FileID=" + cmNewFileId + " Old fileID="
					+ cmOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		}

	}

	// -------------------------------------------------------
	void WriteMapFile(String inpRec) {

		try {

			if (mapFunc == 'O') {
				pathName = inpFileId + "/map";
				if (checkPath(pathName)) {
					fpMap = new PrintStream(new FileOutputStream(inpFileId
							+ "/map/" + mgNewFileId + "-" + mapId + ".esf"));
				} else {
					System.exit(0);
				}
				fpMap.println(rec1);
				fpMap.println(inpRec);
			} else {
				if (mapFunc == 'W') {
					fpMap.println(inpRec);
				} else {
					if (mapFunc == 'C') {
						fpMap.println(inpRec);
						fpMap.close();
						mapFunc = ' ';
						mapEnd = null;
					}
				}
			}

		}

		catch (FileNotFoundException e) {
			System.out.println("IO error: " + e);
			System.out.println("Map fileID=" + mapId + " " + mgNewFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.out.println("Map fileID=" + mapId + " " + mgNewFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		}

	}

	// -------------------------------------------------------
	void WriteMapGroupFile(String inpRec) {

		boolean fnd = false;

		if (mgTabFileIx >= 0) {
			for (int i = 0; i <= mgTabFileIx; i++) {
				if (!mgTabFileId[i].equals("")
						&& mgTabFileId[i].equals(mgNewFileId)) {
					fnd = true;
					break;
				}
			}
		}

		try {

			if (mgNewFileId != mgOldFileId) {
				if (fpMapG != null)
					fpMapG.close();
				pathName = inpFileId + "/mapg";
				if (checkPath(pathName)) {
					fpMapG = new PrintStream(new FileOutputStream(inpFileId
							+ "/mapg/" + mgNewFileId + ".esf", true));
				} else {
					System.exit(0);
				}

				if (!fnd && mgNewFileId != null) {
					mgTabFileIx += 1;
					// System.out.println("Map group notfnd ix=" + mgTabFileIx +
					// " file=" + mgNewFileId);
					mgTabFileId[mgTabFileIx] = mgNewFileId;
					fpMapG.println(rec1);
				}
				fpMapG.println(inpRec);
				mgOldFileId = mgNewFileId;
			} else {
				if (mgOldFileId != null) {
					fpMapG.println(inpRec);
				} else {
					System.out
							.println("Map group FileID notfnd. Rec=" + inpRec);
					System.out.println("Old FileID=" + mgOldFileId);
				}
			}

		}

		catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
			System.out.println("FileID=" + mgNewFileId + " Old fileID="
					+ mgOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.out.println("FileID=" + mgNewFileId + " Old fileID="
					+ mgOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		}

	}

	// -------------------------------------------------------
	void WriteApplicationFile(String inpRec) {

		boolean fnd = false;

		if (apTabFileIx >= 0) {
			for (int i = 0; i <= apTabFileIx; i++) {
				if (!apTabFileId[i].equals("")
						&& apTabFileId[i].equals(apNewFileId)) {
					fnd = true;
					break;
				}
			}
		}

		try {

			if (apNewFileId != apOldFileId) {
				if (fpAppl != null)
					fpAppl.close();
				if (!apEnd.equals("")) {
					pathName = inpFileId + "/table";
					if (checkPath(pathName)) {
						fpAppl = new PrintStream(new FileOutputStream(inpFileId
								+ "/table/" + apNewFileId + ".esf", true));
					} else {
						System.exit(0);
					}
				} else {
					pathName = inpFileId;
					if (checkPath(pathName)) {
						fpAppl = new PrintStream(new FileOutputStream(inpFileId
								+ "/" + apNewFileId + ".esf", true));
					} else {
						System.exit(0);
					}
				}
				// fpAppl.println(rec1);
				if (!fnd && apNewFileId != null) {
					apTabFileIx += 1;
					// System.out.println("Apl notfnd ix=" + apTabFileIx +
					// " file=" + apNewFileId);
					apTabFileId[apTabFileIx] = apNewFileId;
					fpAppl.println(rec1);
				}
				fpAppl.println(inpRec);
				apOldFileId = apNewFileId;
			} else {
				if (apOldFileId != null) {
					fpAppl.println(inpRec);
				} else {
					System.out.println("Appl FileID notfnd. Rec=" + inpRec);
					System.out.println("Old FileID=" + apOldFileId);
				}
			}

		}

		catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
			System.out.println("FileID=" + apNewFileId + " Old fileID="
					+ apOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.out.println("FileID=" + apNewFileId + " Old fileID="
					+ apOldFileId);
			System.out.println("Record=" + inpRec);
			System.exit(0);
		}

	}

	// -------------------------------------------------------
	void CheckFileType(String inpRec) {

		recCnt++;
		recIdx++;

		if (recIdx == 10000) {
			System.out.println("Counter: " + recCnt);
			recIdx = 0;
		}

		if (CheckCommon(inpRec)) {
			WriteCommonFile(inpRec);
			return;
		}

		if (CheckMap(inpRec)) {
			// WriteMapFile(inpRec);
			if (mapFunc == 'C') {
				mapFunc = ' ';
				mapEnd = null;
			}
			WriteMapGroupFile(inpRec);
			return;
		}

		if (inpCustomer.equals("FMS")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27) + "A";
				}
			}
		}

		if (inpCustomer.equals("SMC")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27) + "A";
				}
			}
		}

		if (inpCustomer.equals("NYK")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27) + "A";
				}
			}
		}

		if (inpCustomer.equals("BA")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27) + "A";
				}
			}
		}

		if (inpCustomer.equals("ATP")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27); // ATP
				}
			}
		}

		if (inpCustomer.equals("DSV")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 27); // DSV
				}
			}
		}
		if (inpCustomer.equals("KKI")) {
			if (inpRec.startsWith(":program ") || inpRec.startsWith(":map ")
					|| inpRec.startsWith(":func ")
					|| inpRec.startsWith(":record ")
					|| inpRec.startsWith(":item ")) {
				apEnd = "";
				// apNewFileId = inpRec.substring(23, inpRec.length()-1) + "A";
				if (inpRec.length() <= 27) {
					apNewFileId = inpRec.substring(23, inpRec.length());
				} else {
					apNewFileId = inpRec.substring(23, 28) + "00"; // KK
				}
			}
		}

		if (inpRec.startsWith(":tble ") && inpRec.length() > 23) {
			apNewFileId = inpRec.substring(23, inpRec.length());
			apEnd = ":etble.";
		}

		WriteApplicationFile(inpRec);

	}

	// ********************************************************
	public static void main(String args[]) {

		inpFileId = args[0];

		inpCustomer = args[1]; // MH01 FMS/ATP/DSV/ADG/KKI

		if (inpCustomer.equals(null)) {
			System.out.println("Customer parameter is missing");
			System.exit(0);
		}

		System.out.println("Customer=" + inpCustomer); // MH01

		if (inpCustomer.equals("FMS")) {
			inpPrefix = "F";
			switch (inpFileId.charAt(0)) {
			case 'G':
				inpPrefix = "G";
				break;
			case 'K':
				inpPrefix = "K";
				break;
			case 'D':
				inpPrefix = "D";
				break;
			case 'I':
				inpPrefix = "I";
				break;
			case 'F':
				inpPrefix = "A";
				break;
			case 'C':
				inpPrefix = "L";
				break;
			default:
				inpPrefix = "F";
				break;
			}
		}

		if (inpCustomer.equals("SMC")) {
			inpPrefix = "D"; // dont care - not used
		}

		if (inpCustomer.equals("NYK")) {
			inpPrefix = "K"; // dont care - not used
		}

		if (inpCustomer.equals("BA")) {
			inpPrefix = "C"; // dont care - not used
		}

		if (inpCustomer.equals("KKI")) {
			inpPrefix = inpFileId; // KK
		}

		if (inpCustomer.equals("ATP")) {
			inpPrefix = inpFileId; // ATP
		}

		if (inpCustomer.equals("DSV")) {
			inpPrefix = inpFileId; // DSV
		}

		System.out.println("Prefix=" + inpPrefix);

		// test
		// System.exit(0);
		// test

		String inpRec;
		String newRec = " ";

		String inpName = args[0] + ".esf";

		Vag2Egl2 newVag2Egl2 = new Vag2Egl2();

		try {
			FileInputStream inpFile = new FileInputStream(inpName);
			BufferedReader inpBuf = new BufferedReader(new InputStreamReader(
					inpFile));

			while ((inpRec = inpBuf.readLine()) != null) {
				if (rec1 == null) {
					rec1 = inpRec;
				} else {
					if (inpRec.length() > 0) {
						if (inpRec.charAt(0) != ' ')
							inpRec = inpRec.trim();
						newVag2Egl2.CheckFileType(inpRec);
					}
				}
			}
			inpFile.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO error: " + e);
			System.exit(0);
		}

	}

}
