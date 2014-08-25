package gregtech.api.enums;

import gregtech.api.GregTech_API;
import gregtech.api.enchants.Enchantment_Radioactivity;
import gregtech.api.enums.TC_Aspects.TC_AspectStack;
import gregtech.api.interfaces.IColorModulationContainer;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.objects.MaterialStack;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * This List contains every Material I know about, and is used to determine Recipes for the 
 */
public enum Materials implements IColorModulationContainer {
	/**
	 * This is the Default Material returned in case no Material has been found or a NullPointer has been inserted at a location where it shouldn't happen.
	 * 
	 * Mainly for preventing NullPointer Exceptions and providing Default Values.
	 */
	_NULL				(  -1, Textures.SET_DULL				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"NULL"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, Element._NULL		, Arrays.asList(new TC_AspectStack(TC_Aspects.VACUOS, 1))),
	
	/**
	 * Direct Elements
	 */
	Aluminium			(  19, Textures.SET_DULL				,  10.0F,    128,  2, 1|2  |8      |64|128                  , 128, 200, 240,   0,	"Aluminium"						,    0,       0,          0,          0,       1700, 1700,  true, false,   3,   1,   1, Dyes.dyeLightBlue	, Element.Al		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VOLATUS, 1))),
	Americium			( 103, Textures.SET_METALLIC			,   1.0F,      0,  3, 1|2  |8                               , 200, 200, 200,   0,	"Americium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, Element.Am		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Antimony			(  58, Textures.SET_SHINY				,   1.0F,      0,  2, 1|2  |8                               , 220, 220, 240,   0,	"Antimony"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeLightGray	, Element.Sb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.AQUA, 1))),
	Arsenic				(  39, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255, 255,   0,	"Arsenic"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeOrange		, Element.As		, Arrays.asList(new TC_AspectStack(TC_Aspects.VENENUM, 3))),
	Barium				(  63, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8|16|32                         , 255, 255, 255,   0,	"Barium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, Element.Ba		, Arrays.asList(new TC_AspectStack(TC_Aspects.VINCULUM, 3))),
	Beryllium			(   8, Textures.SET_METALLIC			,  14.0F,     64,  2, 1|2  |8|16|32|64                      , 100, 180, 100,   0,	"Beryllium"						,    0,       0,          0,          0,          0,    0, false, false,   6,   1,   1, Dyes.dyeGreen		, Element.Be		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 1))),
	Bismuth				(  90, Textures.SET_METALLIC			,   6.0F,     64,  1, 1|2  |8      |64|128                  , 100, 160, 160,   0,	"Bismuth"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeCyan		, Element.Bi		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	Boron				(   9, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8|16|32                         , 250, 250, 250,   0,	"Boron"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, Element.B			, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Caesium				(  62, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Caesium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes._NULL			, Element.Cs		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Calcium				(  26, Textures.SET_METALLIC			,   1.0F,      0,  2, 1      |16|32                         , 255, 245, 245,   0,	"Calcium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyePink		, Element.Ca		, Arrays.asList(new TC_AspectStack(TC_Aspects.SANO, 1), new TC_AspectStack(TC_Aspects.TUTAMEN, 1))),
	Carbon				(  10, Textures.SET_DULL				,   1.0F,     64,  2, 1      |16|32|64|128                  ,  20,  20,  20,   0,	"Carbon"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeBlack		, Element.C			, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	Cadmium				(  55, Textures.SET_SHINY				,   1.0F,      0,  2, 1    |8|16|32                         ,  50,  50,  60,   0,	"Cadmium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGray		, Element.Cd		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VENENUM, 1))),
	Cerium				(  65, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Cerium"						,    0,       0,          0,          0,       1068, 1068, true , false,   4,   1,   1, Dyes._NULL			, Element.Ce		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Chlorine			(  23, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255, 255,   0,	"Chlorine"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeCyan		, Element.Cl		, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2), new TC_AspectStack(TC_Aspects.PANNUS, 1))),
	Chrome				(  30, Textures.SET_SHINY				,  11.0F,    256,  3, 1|2  |8      |64|128                  , 255, 230, 230,   0,	"Chrome"						,    0,       0,          0,          0,       1700, 1700,  true, false,   5,   1,   1, Dyes.dyePink		, Element.Cr		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1))),
	Cobalt				(  33, Textures.SET_METALLIC			,   8.0F,    512,  3, 1|2  |8      |64                      ,  80,  80, 250,   0,	"Cobalt"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		, Element.Co		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	Copper				(  35, Textures.SET_SHINY				,   1.0F,      0,  1, 1|2  |8         |128                  , 255, 100,   0,   0,	"Copper"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeOrange		, Element.Cu		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PERMUTATIO, 1))),
	Deuterium			(   2, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255,   0, 240,	"Deuterium"						,    0,       0,          0,          0,          0,    0, false,  true,  10,   1,   1, Dyes.dyeYellow		, Element.D			, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 3))),
	Dysprosium			(  73, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Dysprosium"					,    0,       0,          0,          0,       1680, 1680, true , false,   4,   1,   1, Dyes._NULL			, Element.Dy		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3))),
	Empty				(   0, Textures.SET_EMPTY				,   1.0F,      0,  2,         16|32                         , 255, 255, 255, 255,	"Empty"							,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes._NULL			, Element._NULL		, Arrays.asList(new TC_AspectStack(TC_Aspects.VACUOS, 2))),
	Erbium				(  75, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Erbium"						,    0,       0,          0,          0,       1802, 1802, true , false,   4,   1,   1, Dyes._NULL			, Element.Er		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Europium			(  70, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Europium"						,    0,       0,          0,          0,       1099, 1099, true , false,   4,   1,   1, Dyes._NULL			, Element.Eu		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Fluorine			(  14, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255, 255, 127,	"Fluorine"						,    0,       0,          0,          0,          0,    0, false,  true,   2,   1,   1, Dyes.dyeGreen		, Element.F			, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 2))),
	Gadolinium			(  71, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Gadolinium"					,    0,       0,          0,          0,       1585, 1585, true , false,   4,   1,   1, Dyes._NULL			, Element.Gd		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Gold				(  86, Textures.SET_SHINY				,  12.0F,     64,  2, 1|2  |8      |64|128                  , 255, 255,  30,   0,	"Gold"							,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeYellow		, Element.Au		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 2))),
	Holmium				(  74, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Holmium"						,    0,       0,          0,          0,       1734, 1734, true , false,   4,   1,   1, Dyes._NULL			, Element.Ho		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Hydrogen			(   1, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         ,   0,   0, 255, 240,	"Hydrogen"						,    1,      15,          0,          0,          0,    0, false,  true,   2,   1,   1, Dyes.dyeBlue		, Element.H			, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 1))),
	Helium				(   4, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255,   0, 240,	"Helium"						,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeYellow		, Element.He		, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 2))),
	Helium_3			(   5, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         , 255, 255,   0, 240,	"Helium-3"						,    0,       0,          0,          0,          0,    0, false,  true,  10,   1,   1, Dyes.dyeYellow		, Element.He_3		, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 3))),
	Indium				(  56, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Indium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGray		, Element.In		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Iridium				(  84, Textures.SET_DULL				,   6.0F,   5120,  4, 1|2  |8      |64|128                  , 240, 240, 245,   0,	"Iridium"						,    0,       0,          0,          0,          0,    0, false, false,  10,   1,   1, Dyes.dyeWhite		, Element.Ir		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1))),
	Iron				(  32, Textures.SET_METALLIC			,   6.0F,    256,  2, 1|2  |8      |64|128                  , 200, 200, 200,   0,	"Iron"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, Element.Fe		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3))),
	Lanthanum			(  64, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Lanthanum"						,    0,       0,          0,          0,       1193, 1193, true , false,   4,   1,   1, Dyes._NULL			, Element.La		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Lead				(  89, Textures.SET_DULL				,   8.0F,     64,  1, 1|2  |8      |64|128                  , 140, 100, 140,   0,	"Lead"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyePurple		, Element.Pb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 1))),
	Lithium				(   6, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8|16|32                         , 225, 220, 255,   0,	"Lithium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeLightBlue	, Element.Li		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 2))),
	Lutetium			(  78, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Lutetium"						,    0,       0,          0,          0,       1925, 1925, true , false,   4,   1,   1, Dyes._NULL			, Element.Lu		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Magic				(-128, Textures.SET_SHINY				,   8.0F,   5120,  5, 1|2|4|8|16|32|64|128                  , 100,   0, 200,   0,	"Magic"							,    5,      32,          0,          0,          0,    0, false, false,   7,   1,   1, Dyes.dyePurple		, Element.Ma		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 4))),
	Magnesium			(  18, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 200, 200,   0,	"Magnesium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyePink		, Element.Mg		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.SANO, 1))),
	Manganese			(  31, Textures.SET_DULL				,   7.0F,    512,  2, 1|2  |8      |64                      , 250, 250, 250,   0,	"Manganese"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		, Element.Mn		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3))),
	Mercury				(  87, Textures.SET_SHINY				,   1.0F,      0,  0,         16|32                         , 255, 220, 220,   0,	"Mercury"						,    5,      32,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, Element.Hg		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.AQUA, 1), new TC_AspectStack(TC_Aspects.VENENUM, 1))),
	Niobium				(  47, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Niobium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes._NULL			, Element.Nb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3))),
	Molybdenum			(  48, Textures.SET_SHINY				,   7.0F,    512,  2, 1|2  |8      |64                      , 180, 180, 220,   0,	"Molybdenum"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		, Element.Mo		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	Neodymium			(  67, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Neodymium"						,    0,       0,          0,          0,       1297, 1297, true , false,   4,   1,   1, Dyes._NULL			, Element.Nd		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 2))),
	Neutronium			( 129, Textures.SET_DULL				,   6.0F,  81920,  6, 1|2  |8      |64|128                  , 250, 250, 250,   0,	"Neutronium"					,    0,       0,          0,          0,          0,    0, false, false,  20,   1,   1, Dyes.dyeWhite		, Element.Nt		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.ALIENIS, 2))),
	Nickel				(  34, Textures.SET_METALLIC			,   6.0F,     64,  2, 1|2  |8      |64|128                  , 200, 200, 250,   0,	"Nickel"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeLightBlue	, Element.Ni		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	Nitrogen			(  12, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         ,   0, 150, 200, 240,	"Nitrogen"						,    0,       0,          0,          0,          0,    0, false,  true,   2,   1,   1, Dyes.dyeCyan		, Element.N			, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 2))),
	Osmium				(  83, Textures.SET_METALLIC			,  16.0F,   1280,  4, 1|2  |8      |64|128                  ,  50,  50, 255,   0,	"Osmium"						,    0,       0,          0,          0,          0,    0, false, false,  10,   1,   1, Dyes.dyeBlue		, Element.Os		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1), new TC_AspectStack(TC_Aspects.NEBRISUM, 1))),
	Oxygen				(  13, Textures.SET_FLUID				,   1.0F,      0,  2,         16|32                         ,   0, 100, 200, 240,	"Oxygen"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeWhite		, Element.O			, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 1))),
	Palladium			(  52, Textures.SET_SHINY				,   8.0F,    512,  2, 1|2  |8      |64|128                  , 128, 128, 128,   0,	"Palladium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGray		, Element.Pd		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3))),
	Phosphor			(  21, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8|16|32                         , 255, 255,   0,   0,	"Phosphor"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeYellow		, Element.P			, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 2), new TC_AspectStack(TC_Aspects.POTENTIA, 1))),
	Platinum			(  85, Textures.SET_SHINY				,  12.0F,     64,  2, 1|2  |8      |64|128                  , 255, 255, 200,   0,	"Platinum"						,    0,       0,          0,          0,          0,    0, false, false,   6,   1,   1, Dyes.dyeOrange		, Element.Pt		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.NEBRISUM, 1))),
	Plutonium			( 100, Textures.SET_METALLIC			,   6.0F,    512,  3, 1|2  |8      |64                      , 240,  50,  50,   0,	"Plutonium 244"					,    0,       0,    2000000,          0,          0,    0, false, false,   6,   1,   1, Dyes.dyeLime		, Element.Pu		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 2))),
	Plutonium241		( 101, Textures.SET_SHINY				,   6.0F,    512,  3, 1|2  |8      |64                      , 250,  70,  70,   0,	"Plutonium 241"					,    0,       0,    2000000,          0,          0,    0, false, false,   6,   1,   1, Dyes.dyeLime		, Element.Pu_241	, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 3))),
	Potassium			(  25, Textures.SET_METALLIC			,   1.0F,      0,  1, 1      |16|32                         , 250, 250, 250,   0,	"Potassium"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeWhite		, Element.K			, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 1))),
	Praseodymium		(  66, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Praseodymium"					,    0,       0,          0,          0,       1208, 1208, true , false,   4,   1,   1, Dyes._NULL			, Element.Pr		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Promethium			(  68, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Promethium"					,    0,       0,          0,          0,       1315, 1315, true , false,   4,   1,   1, Dyes._NULL			, Element.Pm		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Rubidium			(  43, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               , 240,  30,  30,   0,	"Rubidium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeRed			, Element.Rb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1))),
	Samarium			(  69, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Samarium"						,    0,       0,          0,          0,       1345, 1345, true , false,   4,   1,   1, Dyes._NULL			, Element.Sm		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Scandium			(  27, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Scandium"						,    0,       0,          0,          0,       1814, 1814, true , false,   2,   1,   1, Dyes.dyeYellow		, Element.Sc		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Silicon				(  20, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         ,  60,  60,  80,   0,	"Silicon"						,    0,       0,          0,          0,       1500, 1500, true , false,   1,   1,   1, Dyes.dyeBlack		, Element.Si		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.TENEBRAE, 1))),
	Silver				(  54, Textures.SET_SHINY				,  10.0F,     64,  2, 1|2  |8      |64|128                  , 220, 220, 255,   0,	"Silver"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, Element.Ag		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 1))),
	Sodium				(  17, Textures.SET_METALLIC			,   1.0F,      0,  2, 1      |16|32                         ,   0,   0, 150,   0,	"Sodium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		, Element.Na		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.LUX, 1))),
	Strontium			(  44, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 200, 200, 200,   0,	"Strontium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Element.Sr		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.STRONTIO, 1))),
	Sulfur				(  22, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8|16|32                         , 200, 200,   0,   0,	"Sulfur"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeYellow		, Element.S			, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	Tantalum			(  80, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Tantalum"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes._NULL			, Element.Ta		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VINCULUM, 1))),
	Tellurium			(  59, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Tellurium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGray		, Element.Te		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Terbium				(  72, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Terbium"						,    0,       0,          0,          0,       1629, 1629, true , false,   4,   1,   1, Dyes._NULL			, Element.Tb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Thorium				(  96, Textures.SET_SHINY				,   6.0F,    512,  2, 1|2  |8      |64                      ,   0,  30,   0,   0,	"Thorium"						,    0,       0,     500000,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeBlack		, Element.Th		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Thulium				(  76, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Thulium"						,    0,       0,          0,          0,       1818, 1818, true , false,   4,   1,   1, Dyes._NULL			, Element.Tm		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Tin					(  57, Textures.SET_DULL				,   1.0F,      0,  1, 1|2  |8         |128                  , 220, 220, 220,   0,	"Tin"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		, Element.Sn		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1))),
	Titanium			(  28, Textures.SET_METALLIC			,   8.0F,   2560,  3, 1|2  |8      |64|128                  , 220, 160, 240,   0,	"Titanium"						,    0,       0,          0,          0,       1500, 1500, true , false,   5,   1,   1, Dyes.dyePurple		, Element.Ti		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.TUTAMEN, 1))),
	Tritium				(   3, Textures.SET_METALLIC			,   1.0F,      0,  2,         16|32                         , 255,   0,   0, 240,	"Tritium"						,    0,       0,          0,          0,          0,    0, false,  true,  10,   1,   1, Dyes.dyeRed			, Element.T			, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 4))),
	Tungsten			(  81, Textures.SET_METALLIC			,   8.0F,   5120,  3, 1|2  |8      |64|128                  ,  50,  50,  50,   0,	"Tungsten"						,    0,       0,          0,          0,       2500, 2500, true , false,   4,   1,   1, Dyes.dyeBlack		, Element.W			, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.TUTAMEN, 1))),
	Uranium				(  98, Textures.SET_METALLIC			,   6.0F,    512,  3, 1|2  |8      |64                      ,  50, 240,  50,   0,	"Uranium 238"					,    0,       0,    1000000,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGreen		, Element.U			, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Uranium235			(  97, Textures.SET_SHINY				,   6.0F,    512,  3, 1|2  |8      |64                      ,  70, 250,  70,   0,	"Uranium 235"					,    0,       0,    1000000,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGreen		, Element.U_235		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 2))),
	Vanadium			(  29, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8                               ,  50,  50,  50,   0,	"Vanadium"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeBlack		, Element.V			, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Ytterbium			(  77, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Ytterbium"						,    0,       0,          0,          0,       1097, 1097, true , false,   4,   1,   1, Dyes._NULL			, Element.Yb		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Yttrium				(  45, Textures.SET_METALLIC			,   1.0F,      0,  2, 1|2  |8|16|32                         , 255, 255, 255,   0,	"Yttrium"						,    0,       0,          0,          0,       1799, 1799, true , false,   4,   1,   1, Dyes._NULL			, Element.Y			, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1))),
	Zinc				(  36, Textures.SET_METALLIC			,   1.0F,      0,  1, 1|2  |8                               , 250, 240, 240,   0,	"Zinc"							,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeWhite		, Element.Zn		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.SANO, 1))),
	
	/**
	 * The "Random Material" ones.
	 */
	Organic				(  -1, Textures.SET_LEAF				,   1.0F,      0,  1, false),
	Crystal				(  -1, Textures.SET_SHINY				,   1.0F,      0,  3, false),
	Quartz				(  -1, Textures.SET_QUARTZ				,   1.0F,      0,  2, false),
	Metal				(  -1, Textures.SET_METALLIC			,   1.0F,      0,  2, false),
	Cobblestone			(  -1, Textures.SET_DULL				,   1.0F,      0,  1, false),
	Brick				(  -1, Textures.SET_DULL				,   1.0F,      0,  1, false),
	BrickNether			(  -1, Textures.SET_DULL				,   1.0F,      0,  1, false),
	
	/**
	 * Unknown Material Components. Dead End Section.
	 */
	
	DarkSteel			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Dark Steel"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Terrasteel			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Terra Steel"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	ConductiveIron		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Conductive Iron"				,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	ElectricalSteel		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Electrical Steel"				,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	EnergeticAlloy		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Energetic Alloy"				,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	VibrantAlloy		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Vibrant Alloy"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	PulsatingIron		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Pulsating Iron"				,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Rutile				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Rutile"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Fluix				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Fluix"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Manasteel			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Manasteel"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Tennantite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Tennantite"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	DarkThaumium		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Dark Thaumium"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Alfium				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Alfium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Ryu					(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Ryu"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Mutation			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Mutation"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Aquamarine			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Aquamarine"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Ender				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Ender"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	ElvenElementium		(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Elven Elementium"				,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	
	Adamantium			( 319, Textures.SET_SHINY				,  10.0F,   5120,  5, 1|2  |8      |64|128                  , 255, 255, 255,   0,	"Adamantium"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	),
	Adamite				(  -1, Textures.SET_NONE				,   1.0F,      0,  3, 1    |8                               , 255, 255, 255,   0,	"Adamite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	),
	Adluorite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Adluorite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Agate				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Agate"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Alduorite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Alduorite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Amber				( 514, Textures.SET_RUBY				,   1.0F,      0,  3, 1  |4|8                               , 255, 128,   0, 127,	"Amber"							,    5,       3,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeOrange		, Arrays.asList(new TC_AspectStack(TC_Aspects.VINCULUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1))),
	Ammonium			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Ammonium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Amordrine			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Amordrine"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Andesite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Andesite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Angmallen			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Angmallen"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Ardite				(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255,   0,   0,   0,	"Ardite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Aredrite			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255,   0,   0,   0,	"Aredrite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Atlarus				(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Atlarus"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Bitumen				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Bitumen"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Black				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 0                                     ,   0,   0,   0,   0,	"Black"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlack		),
	Blizz				( 851, Textures.SET_SHINY				,   1.0F,      0,  2, 1                                     , 220, 233, 255,   0,	"Blizz"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Blueschist			( 852, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Blueschist"					,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeLightBlue	),
	Bluestone			( 813, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Bluestone"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		),
	Bloodstone			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Bloodstone"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			),
	Blutonium			(  -1, Textures.SET_SHINY				,   1.0F,      0,  2, 1|2  |8                               ,   0,   0, 255,   0,	"Blutonium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		),
	Carmot				(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Carmot"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Celenegil			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Celenegil"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	CertusQuartz		( 516, Textures.SET_QUARTZ				,   5.0F,     32,  1, 1  |4|8      |64                      , 210, 210, 230,   0,	"Certus Quartz"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VITREUS, 1))),
	Ceruclase			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Ceruclase"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Citrine				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Citrine"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	CobaltHexahydrate	( 853, Textures.SET_METALLIC			,   1.0F,      0,  2, 1      |16                            ,  80,  80, 250,   0,	"Cobalt Hexahydrate"			,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		),
	ConstructionFoam	( 854, Textures.SET_DULL				,   1.0F,      0,  2, 1      |16                            , 128, 128, 128,   0,	"Construction Foam"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		),
	Chalk				( 856, Textures.SET_FINE				,   1.0F,      0,  2, 1                                     , 250, 250, 250,   0,	"Chalk"							,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeWhite		),
	Chert				( 857, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Chert"							,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes._NULL			),
	Chimerite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Chimerite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Coral				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 128, 255,   0,	"Coral"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	CrudeOil			( 858, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     ,  10,  10,  10,   0,	"Crude Oil"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		),
	Chrysocolla			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Chrysocolla"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	CrystalFlux			( 517, Textures.SET_QUARTZ				,   1.0F,      0,  3, 1  |4                                 , 100,  50, 100,   0,	"Flux Crystal"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Cyanite				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Cyanite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeCyan		),
	Dacite				( 859, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Dacite"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeLightGray	),
	DarkIron			( 342, Textures.SET_DULL				,   7.0F,    384,  3, 1|2  |8      |64                      ,  55,  40,  60,   0,	"Dark Iron"						,    0,       0,          0,          0,          0,    0, false, false,   5,   1,   1, Dyes.dyePurple		),
	DarkStone			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Dark Stone"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlack		),
	Demonite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Demonite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			),
	Desh				( 884, Textures.SET_DULL				,   1.0F,      0,  2, 1|2  |8                               ,  40,  40,  40,   0,	"Desh"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		),
	Desichalkos			(  -1, Textures.SET_NONE				,   6.0F,   1280,  3, 1|2  |8      |64                      , 255, 255, 255,   0,	"Desichalkos"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Dilithium			( 515, Textures.SET_DIAMOND				,   1.0F,      0,  1, 1  |4|8|16                            , 255, 250, 250, 127,	"Dilithium"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeWhite		),
	Draconic			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Draconic"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			),
	Duranium			( 328, Textures.SET_METALLIC			,   8.0F,   1280,  4, 1|2          |64                      , 255, 255, 255,   0,	"Duranium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	),
	Eclogite			( 860, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Eclogite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	ElectrumFlux		( 320, Textures.SET_SHINY				,  16.0F,    512,  3, 1|2          |64                      , 255, 255, 120,   0,	"Fluxed Electrum"				,    0,       0,          0,          0,       3000, 3000, true , false,   1,   1,   1, Dyes.dyeYellow		),
	Emery				( 861, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Emery"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Enderium			( 321, Textures.SET_DULL				,   8.0F,    256,  3, 1|2          |64                      ,  89, 145, 135,   0,	"Enderium"						,    0,       0,          0,          0,       3000, 3000, true , false,   1,   1,   1, Dyes.dyeGreen		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ALIENIS, 1))),
	Energized			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 0                                     , 255, 255, 255,   0,	"Energized"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Epidote				( 862, Textures.SET_DULL				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Epidote"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes._NULL			),
	Eximite				(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Eximite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	FieryBlood			( 346, Textures.SET_SHINY				,   8.0F,    256,  3, 1|2          |64                      ,  64,   0,   0,   0,	"Fiery Blood"					,    5,    2048,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 3), new TC_AspectStack(TC_Aspects.CORPUS, 3))),
	Firestone			( 347, Textures.SET_QUARTZ				,   6.0F,   1280,  3, 1  |4|8      |64                      , 200,  20,   0,   0,	"Firestone"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			),
	Fluorite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1    |8                               , 255, 255, 255,   0,	"Fluorite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		),
	FoolsRuby			( 512, Textures.SET_RUBY				,   1.0F,      0,  2, 1  |4|8                               , 255, 100, 100, 127,	"Ruby"							,    0,       0,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 2))),
	Force				( 521, Textures.SET_DIAMOND				,  10.0F,    128,  3, 1|2|4|8      |64|128                  , 255, 255,   0,   0,	"Force"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 5))),
	Forcicium			( 518, Textures.SET_DIAMOND				,   1.0F,      0,  1, 1  |4|8|16                            ,  50,  50,  70,   0,	"Forcicium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2))),
	Forcillium			( 519, Textures.SET_DIAMOND				,   1.0F,      0,  1, 1  |4|8|16                            ,  50,  50,  70,   0,	"Forcillium"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2))),
	Gabbro				( 863, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Gabbro"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes._NULL			),
	Glowstone			( 811, Textures.SET_SHINY				,   1.0F,      0,  1, 1      |16                            , 255, 255,   0,   0,	"Glowstone"						,    0,       0,      25000,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, Arrays.asList(new TC_AspectStack(TC_Aspects.LUX, 2), new TC_AspectStack(TC_Aspects.SENSUS, 1))),
	Gneiss				( 864, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Gneiss"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes._NULL			),
	Graphite			( 865, Textures.SET_DULL				,   5.0F,     32,  2, 1    |8      |64|128                  , 128, 128, 128,   0,	"Graphite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGray		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	Graphene			( 819, Textures.SET_DULL				,   6.0F,     32,  1, 1            |64|128                  , 128, 128, 128,   0,	"Graphene"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGray		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.ELECTRUM, 1))),
	Greenschist			( 866, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Green Schist"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGreen		),
	Greenstone			( 867, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Greenstone"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGreen		),
	Greywacke			( 868, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Greywacke"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		),
	Haderoth			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Haderoth"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Hematite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Hematite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Hepatizon			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Hepatizon"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	HSLA				( 322, Textures.SET_METALLIC			,   6.0F,    500,  3, 1|2          |64|128                  , 128, 128, 128,   0,	"HSLA Steel"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.ORDO, 1))),
	Ignatius			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Ignatius"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Infernal			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 0                                     , 255, 255, 255,   0,	"Infernal"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Infuscolium			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Infuscolium"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	InfusedGold			( 323, Textures.SET_SHINY				,  12.0F,     64,  3, 1|2  |8      |64|128                  , 255, 200,  60,   0,	"Infused Gold"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		),
	InfusedAir			( 540, Textures.SET_SHARDS				,   8.0F,     64,  3, 1  |4|8      |64|128                  , 255, 255,   0,   0,	"Aer"							,    5,     160,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeYellow		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.AER, 2))),
	InfusedFire			( 541, Textures.SET_SHARDS				,   8.0F,     64,  3, 1  |4|8      |64|128                  , 255,   0,   0,   0,	"Ignis"							,    5,     320,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.IGNIS, 2))),
	InfusedEarth		( 542, Textures.SET_SHARDS				,   8.0F,    256,  3, 1  |4|8      |64|128                  ,   0, 255,   0,   0,	"Terra"							,    5,     160,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeGreen		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.TERRA, 2))),
	InfusedWater		( 543, Textures.SET_SHARDS				,   8.0F,     64,  3, 1  |4|8      |64|128                  ,   0,   0, 255,   0,	"Aqua"							,    5,     160,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeBlue		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.AQUA, 2))),
	InfusedEntropy		( 544, Textures.SET_SHARDS				,  32.0F,     64,  4, 1  |4|8      |64|128                  ,  62,  62,  62,   0,	"Perditio"						,    5,     320,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeBlack		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.PERDITIO, 2))),
	InfusedOrder		( 545, Textures.SET_SHARDS				,   8.0F,     64,  3, 1  |4|8      |64|128                  , 252, 252, 252,   0,	"Ordo"							,    5,     240,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.ORDO, 2))),
	InfusedVis			(  -1, Textures.SET_SHARDS				,   8.0F,     64,  3, 1  |4|8      |64|128                  , 255,   0, 255,   0,	"Auram"							,    5,     240,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyePurple		, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.AURAM, 2))),
	InfusedDull			(  -1, Textures.SET_SHARDS				,  32.0F,     64,  3, 1  |4|8      |64|128                  , 100, 100, 100,   0,	"Vacuus"						,    5,     160,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.VACUOS, 2))),
	Inolashite			(  -1, Textures.SET_NONE				,   6.0F,    128,  3, 1|2  |8      |64                      , 255, 255, 255,   0,	"Inolashite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Invisium			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Invisium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Jade				( 537, Textures.SET_SHINY				,   1.0F,      0,  2, 1    |8                               ,   0, 100,   0,   0,	"Jade"							,    0,       0,          0,          0,          0,    0, false, false,   5,   1,   1, Dyes.dyeGreen		, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Jasper				( 511, Textures.SET_EMERALD				,   1.0F,      0,  2, 1  |4|8                               , 200,  80,  80, 100,	"Jasper"						,    0,       0,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 2))),
	Kalendrite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Kalendrite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Komatiite			( 869, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Komatiite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Lava				( 700, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            , 255,  64,   0,   0,	"Lava"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		),
	Lemurite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1                                     , 255, 255, 255,   0,	"Lemurite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Limestone			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Limestone"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Lodestone			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Lodestone"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Luminite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 250, 250, 250,   0,	"Luminite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		),
	Magma				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255,  64,   0,   0,	"Magma"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		),
	Mawsitsit			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Mawsitsit"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Mercassium			(  -1, Textures.SET_NONE				,   6.0F,     64,  1, 1|2  |8      |64                      , 255, 255, 255,   0,	"Mercassium"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	MeteoricIron		( 340, Textures.SET_METALLIC			,   6.0F,    384,  2, 1|2  |8      |64                      , 100,  50,  80,   0,	"Meteoric Iron"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))),
	MeteoricSteel		( 341, Textures.SET_METALLIC			,   6.0F,    768,  2, 1|2          |64                      ,  50,  25,  40,   0,	"Meteoric Steel"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1), new TC_AspectStack(TC_Aspects.ORDO, 1))),
	Meteorite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               ,  80,  35,  60,   0,	"Meteorite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		),
	Meutoite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Meutoite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Migmatite			( 872, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Migmatite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Mimichite			(  -1, Textures.SET_GEM_VERTICAL		,   1.0F,      0,  1, 1  |4|8                               , 255, 255, 255,   0,	"Mimichite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Monazite			( 520, Textures.SET_DIAMOND				,   1.0F,      0,  1, 1  |4|8                               ,  50,  70,  50,   0,	"Monazite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		),
	Moonstone			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Moonstone"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.ALIENIS, 1))),
	Naquadah			( 324, Textures.SET_METALLIC			,   6.0F,   1280,  4, 1|2  |8|16   |64                      ,  50,  50,  50,   0,	"Naquadah"						,    0,       0,          0,          0,       3000, 3000, true , false,  10,   1,   1, Dyes.dyeBlack		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.RADIO, 1), new TC_AspectStack(TC_Aspects.NEBRISUM, 1))),
	NaquadahAlloy		( 325, Textures.SET_METALLIC			,   8.0F,   5120,  5, 1|2          |64|128                  ,  40,  40,  40,   0,	"Naquadah Alloy"				,    0,       0,          0,          0,       3000, 3000, true , false,  10,   1,   1, Dyes.dyeBlack		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.NEBRISUM, 1))),
	NaquadahEnriched	( 326, Textures.SET_METALLIC			,   6.0F,   1280,  4, 1|2  |8|16   |64                      ,  50,  50,  50,   0,	"Enriched Naquadah"				,    0,       0,          0,          0,       3000, 3000, true , false,  15,   1,   1, Dyes.dyeBlack		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.RADIO, 2), new TC_AspectStack(TC_Aspects.NEBRISUM, 2))),
	Naquadria			( 327, Textures.SET_SHINY				,   1.0F,    512,  4, 1|2  |8      |64                      ,  30,  30,  30,   0,	"Naquadria"						,    0,       0,          0,          0,       3000, 3000, true , false,  20,   1,   1, Dyes.dyeBlack		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.RADIO, 3), new TC_AspectStack(TC_Aspects.NEBRISUM, 3))),
	Nether				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Nether"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	NetherBrick			( 814, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 100,   0,   0,   0,	"Nether Brick"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	NetherQuartz		( 522, Textures.SET_QUARTZ				,   1.0F,     32,  1, 1  |4|8      |64                      , 230, 210, 210,   0,	"Nether Quartz"					,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VITREUS, 1))),
	NetherStar			( 506, Textures.SET_NETHERSTAR			,   1.0F,   5120,  4, 1  |4        |64                      , 255, 255, 255,   0,	"Nether Star"					,    5,   50000,          0,          0,          0,    0, false, false,  15,   1,   1, Dyes.dyeWhite		),
	Nikolite			( 812, Textures.SET_SHINY				,   1.0F,      0,  1, 1    |8                               ,  60, 180, 200,   0,	"Nikolite"						,    0,       0,       5000,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeCyan		, Arrays.asList(new TC_AspectStack(TC_Aspects.ELECTRUM, 2))),
	ObsidianFlux		(  -1, Textures.SET_DULL				,   1.0F,      0,  1, 1|2                                   ,  80,  50, 100,   0,	"Fluxed Obsidian"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		),
	Oilsands			( 878, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               ,  10,  10,  10,   0,	"Oilsands"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Onyx				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Onyx"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Orichalcum			(  -1, Textures.SET_NONE				,   6.0F,     64,  3, 1|2  |8      |64                      , 255, 255, 255,   0,	"Orichalcum"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Osmonium			(  -1, Textures.SET_NONE				,   6.0F,     64,  1, 1|2  |8      |64                      , 255, 255, 255,   0,	"Osmonium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		),
	Oureclase			(  -1, Textures.SET_NONE				,   6.0F,     64,  1, 1|2  |8      |64                      , 255, 255, 255,   0,	"Oureclase"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Painite				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Painite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Peanutwood			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Peanut Wood"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Petroleum			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Petroleum"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Pewter				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Pewter"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Phoenixite			(  -1, Textures.SET_NONE				,   6.0F,     64,  1, 1|2  |8      |64                      , 255, 255, 255,   0,	"Phoenixite"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Potash				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Potash"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Prometheum			(  -1, Textures.SET_NONE				,   6.0F,     64,  1, 1|2  |8      |64                      , 255, 255, 255,   0,	"Prometheum"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Quartzite			( 523, Textures.SET_QUARTZ				,   1.0F,      0,  1, 1  |4|8                               , 210, 230, 210,   0,	"Quartzite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		),
	Quicklime			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Quicklime"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Randomite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Randomite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	RefinedGlowstone	(-326, Textures.SET_METALLIC			,   1.0F,      0,  1, 1|2                                   , 255, 255,   0,   0,	"Refined Glowstone"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	RefinedObsidian		(-327, Textures.SET_METALLIC			,   1.0F,      0,  1, 1|2                                   ,  80,  50, 100,   0,	"Refined Obsidian"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		),
	Rhyolite			( 875, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Rhyolite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Rubracium			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Rubracium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	RyuDragonRyder		(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Ryu Dragon Ryder"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Sand				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Sand"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Sanguinite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1|2  |8                               , 255, 255, 255,   0,	"Sanguinite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Siltstone			( 876, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Siltstone"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Spinel				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255,   0,	"Spinel"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Starconium			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1|2  |8                               , 255, 255, 255,   0,	"Starconium"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Sugilite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Sugilite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Sunstone			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Sunstone"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.ALIENIS, 1))),
	Tar					(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     ,  10,  10,  10,   0,	"Tar"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		),
	Tartarite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1|2  |8                               , 255, 255, 255,   0,	"Tartarite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Tapazite			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Tapazite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		),
	Thyrium				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1|2  |8                               , 255, 255, 255,   0,	"Thyrium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Tourmaline			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Tourmaline"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	Tritanium			( 329, Textures.SET_METALLIC			,   6.0F,   2560,  4, 1|2          |64                      , 255, 255, 255,   0,	"Tritanium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 2))),
	Turquoise			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Turquoise"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes._NULL			),
	UUAmplifier			( 721, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            ,  96,   0, 128,   0,	"UU-Amplifier"					,    0,       0,          0,          0,          0,    0, false, false,  10,   1,   1, Dyes.dyePink		),
	UUMatter			( 703, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            , 128,   0, 196,   0,	"UU-Matter"						,    0,       0,          0,          0,          0,    0, false, false,  10,   1,   1, Dyes.dyePink		),
	Void				(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255, 200,	"Void"							,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes._NULL			, Arrays.asList(new TC_AspectStack(TC_Aspects.VACUOS, 1))),
	Voidstone			(  -1, Textures.SET_NONE				,   1.0F,      0,  1, 0                                     , 255, 255, 255, 200,	"Voidstone"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes._NULL			, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.VACUOS, 1))),
	Vulcanite			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Vulcanite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Vyroxeres			(  -1, Textures.SET_NONE				,   6.0F,     64,  2, 1|2  |8      |64                      , 255, 255, 255,   0,	"Vyroxeres"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			),
	Wimalite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2,       8                               , 255, 255, 255,   0,	"Wimalite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		),
	Yellorite			(  -1, Textures.SET_NONE				,   1.0F,      0,  2,       8                               , 255, 255, 255,   0,	"Yellorite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		),
	Yellorium			(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1|2                                   , 255, 255, 255,   0,	"Yellorium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		),
	Zectium				(  -1, Textures.SET_NONE				,   1.0F,      0,  2, 1|2  |8                               , 255, 255, 255,   0,	"Zectium"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlack		),
	
	/**
	 * Circuitry, Batteries and other Technical things
	 */
	Primitive			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Primitive"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 1))),
	Basic				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Basic"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 2))),
	Good				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Good"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 3))),
	Advanced			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Advanced"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 4))),
	Data				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Data"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 5))),
	Elite				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Elite"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 6))),
	Master				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Master"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 7))),
	Ultimate			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Ultimate"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 8))),
	Infinite			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Infinite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	),
	
	/**
	 * Not possible to determine exact Components
	 */
	Antimatter			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Antimatter"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 9), new TC_AspectStack(TC_Aspects.PERFODIO, 8))),
	BioFuel				( 705, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 255, 128,   0,   0,	"Biofuel"						,    0,       6,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		),
	Biomass				( 704, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            ,   0, 255,   0,   0,	"Biomass"						,    3,       8,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGreen		),
	Chocolate			( 886, Textures.SET_FINE				,   1.0F,      0,  0, 1                                     , 190,  95,   0,   0,	"Chocolate"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		),
	Cluster				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255, 127,	"Cluster"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeWhite		),
	CoalFuel			( 710, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            ,  50,  50,  70,   0,	"Coalfuel"						,    0,      16,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		),
	Cocoa				( 887, Textures.SET_FINE				,   1.0F,      0,  0, 1                                     , 190,  95,   0,   0,	"Cocoa"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		),
	Coffee				( 888, Textures.SET_FINE				,   1.0F,      0,  0, 1                                     , 150,  75,   0,   0,	"Coffee"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		),
	Creosote			( 712, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 128,  64,   0,   0,	"Creosote"						,    3,       8,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		),
	Ethanol				( 706, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 255, 128,   0,   0,	"Ethanol"						,    0,     128,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		),
	Fuel				( 708, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 255, 255,   0,   0,	"Diesel"						,    0,     128,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Gunpowder			( 800, Textures.SET_DULL				,   1.0F,      0,  0, 1                                     , 128, 128, 128,   0,	"Gunpowder"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 4))),
	Honey				( 725, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 210, 200,   0,   0,	"Honey"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	LimePure			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Pure Lime"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		),
	Lubricant			( 724, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 255, 196,   0,   0,	"Lubricant"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1))),
	Meat				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Meat"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		),
	MeatRaw				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Raw Meat"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		),
	MeatCooked			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Cooked Meat"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		),
	Milk				( 885, Textures.SET_FINE				,   1.0F,      0,  0, 1      |16                            , 254, 254, 254,   0,	"Milk"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.SANO, 2))),
	Mud					(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Mud"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		),
	Oil					( 707, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            ,  10,  10,  10,   0,	"Oil"							,    3,      16,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		),
	Paper				( 879, Textures.SET_PAPER				,   1.0F,      0,  0, 1                                     , 250, 250, 250,   0,	"Paper"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.COGNITO, 1))),
	Peat				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Peat"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2), new TC_AspectStack(TC_Aspects.IGNIS, 2))),
	Quantum				(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Quantum"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		),
	Red					(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255,   0,   0,   0,	"Red"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			),
	Reinforced			(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"Reinforced"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		),
	SeedOil				( 713, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 196, 255,   0,   0,	"Seed Oil"						,    3,       2,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2))),
	SeedOilHemp			( 722, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 196, 255,   0,   0,	"Hemp Seed Oil"					,    3,       2,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2))),
	SeedOilLin			( 723, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            , 196, 255,   0,   0,	"Lin Seed Oil"					,    3,       2,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2))),
	Stone				( 299, Textures.SET_ROUGH				,   4.0F,     32,  1, 1            |64|128                  , 205, 205, 205,   0,	"Stone"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1))),
	TNT					(  -1, Textures.SET_NONE				,   1.0F,      0,  0, 0                                     , 255, 255, 255,   0,	"TNT"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 7), new TC_AspectStack(TC_Aspects.IGNIS, 4))),
	Unstable			(  -1, Textures.SET_NONE				,   1.0F,      0,  4, 0                                     , 255, 255, 255, 127,	"Unstable"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 4))),
	Unstableingot		(  -1, Textures.SET_NONE				,   1.0F,      0,  4, 0                                     , 255, 255, 255, 127,	"Unstable"						,    0,       0,          0,          0,          0,    0, false,  true,   1,   1,   1, Dyes.dyeWhite		, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 4))),
	Wheat				( 881, Textures.SET_POWDER				,   1.0F,      0,  0, 1                                     , 255, 255, 196,   0,	"Wheat"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, Arrays.asList(new TC_AspectStack(TC_Aspects.MESSIS, 2))),
	Wood				( 809, Textures.SET_WOOD				,   2.0F,     16,  0, 1            |64|128                  , 100,  50,   0,   0,	"Wood"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, Arrays.asList(new TC_AspectStack(TC_Aspects.ARBOR, 2))),
	WoodSealed			( 889, Textures.SET_WOOD				,   3.0F,     24,  0, 1|2          |64|128                  ,  80,  40,   0,   0,	"Sealed Wood"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, Arrays.asList(new TC_AspectStack(TC_Aspects.ARBOR, 2), new TC_AspectStack(TC_Aspects.FABRICO, 1))),
	
	/**
	 * TODO: This
	 */
	AluminiumBrass		(  -1, Textures.SET_METALLIC			,   6.0F,     64,  2, 1|2          |64                      , 255, 255, 255,   0,	"Aluminium Brass"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Osmiridium			( 317, Textures.SET_METALLIC			,   8.0F,   3000,  4, 1|2          |64|128                  , 100, 100, 255,   0,	"Osmiridium"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightBlue	),
	Sunnarium			( 318, Textures.SET_SHINY				,   1.0F,      0,  1, 1|2                                   , 255, 255,   0,   0,	"Sunnarium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		),
	Endstone			( 808, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 255, 255, 255,   0,	"Endstone"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeYellow		),
	Netherrack			( 807, Textures.SET_DULL				,   1.0F,      0,  0, 1                                     , 200,   0,   0,   0,	"Netherrack"					,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeRed			),
	SoulSand			(  -1, Textures.SET_DULL				,   1.0F,      0,  0, 1                                     , 255, 255, 255,   0,	"Soulsand"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeBrown		),
	
	/**
	 * First Degree Compounds
	 */
	Almandine			( 820, Textures.SET_ROUGH				,   1.0F,      0,  1, 1    |8                               , 255,   0,   0,   0,	"Almandine"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Iron, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	Andradite			( 821, Textures.SET_ROUGH				,   1.0F,      0,  1, 1    |8                               , 150, 120,   0,   0,	"Andradite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Iron, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	AnnealedCopper		( 345, Textures.SET_SHINY				,   1.0F,      0,  2, 1|2             |128                  , 255, 120,  20,   0,	"Annealed Copper"				,    0,       0,          0,          0,       1200, 1200,  true, false,   3,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Copper, 1))),
	Asbestos			( 946, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 230, 230, 230,   0,	"Asbestos"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 4), new MaterialStack(Oxygen, 9))), // Mg3Si2O5(OH)4
	Ash					( 815, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     , 150, 150, 150,   0,	"Ashes"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, 2, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 1))),
	BandedIron			( 917, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 145,  90,  90,   0,	"Banded Iron"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, 1, Arrays.asList(new MaterialStack(Iron, 2), new MaterialStack(Oxygen, 3))),
	BatteryAlloy		( 315, Textures.SET_DULL				,   1.0F,      0,  1, 1|2                                   , 156, 124, 160,   0,	"Battery Alloy"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		, 2, Arrays.asList(new MaterialStack(Lead, 4), new MaterialStack(Antimony, 1))),
	Bauxite				( 822, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 200, 100,   0,   0,	"Bauxite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBrown		, 1, Arrays.asList(new MaterialStack(Titanium, 1), new MaterialStack(Aluminium, 16), new MaterialStack(Hydrogen, 10), new MaterialStack(Oxygen, 12))),
	BlueTopaz			( 513, Textures.SET_GEM_HORIZONTAL		,   7.0F,    256,  3, 1  |4|8      |64                      ,   0,   0, 255, 127,	"Blue Topaz"					,    0,       0,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Fluorine, 2), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4))),
	Bone				( 806, Textures.SET_DULL				,   1.0F,      0,  1, 0                                     , 250, 250, 250,   0,	"Bone"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 0, Arrays.asList(new MaterialStack(Calcium, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.MORTUUS, 2), new TC_AspectStack(TC_Aspects.CORPUS, 1))),
	Brass				( 301, Textures.SET_METALLIC			,   7.0F,     96,  1, 1|2          |64|128                  , 255, 180,   0,   0,	"Brass"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Zinc, 1), new MaterialStack(Copper, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	Bronze				( 300, Textures.SET_METALLIC			,   6.0F,    192,  2, 1|2          |64|128                  , 255, 128,   0,   0,	"Bronze"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Copper, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	BrownLimonite		( 930, Textures.SET_METALLIC			,   1.0F,      0,  1, 1    |8                               , 200, 100,   0,   0,	"Brown Limonite"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 2))), // FeO(OH)
	Calcite				( 823, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 250, 230, 220,   0,	"Calcite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 3))),
	Cassiterite			( 824, Textures.SET_METALLIC			,   1.0F,      0,  1,       8                               , 220, 220, 220,   0,	"Cassiterite"					,    0,       0,          0,          0,          0,    0, false, false,   4,   3,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Oxygen, 2))),
	CassiteriteSand		( 937, Textures.SET_SAND				,   1.0F,      0,  1,       8                               , 220, 220, 220,   0,	"Cassiterite Sand"				,    0,       0,          0,          0,          0,    0, false, false,   4,   3,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Oxygen, 2))),
	Celestine			( 913, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 200, 205, 240,   0,	"Celestine"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, 1, Arrays.asList(new MaterialStack(Strontium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4))),
	Chalcopyrite		( 855, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 160, 120,  40,   0,	"Chalcopyrite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, 1, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Iron, 1), new MaterialStack(Sulfur, 2))),
	Charcoal			( 536, Textures.SET_FINE				,   1.0F,      0,  1, 1  |4                                 , 100,  70,  70,   0,	"Charcoal"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2), new TC_AspectStack(TC_Aspects.IGNIS, 2))),
	Chromite			( 825, Textures.SET_METALLIC			,   1.0F,      0,  1, 1    |8                               ,  35,  20,  15,   0,	"Chromite"						,    0,       0,          0,          0,       1700, 1700,  true, false,   6,   1,   1, Dyes.dyePink		, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Chrome, 2), new MaterialStack(Oxygen, 4))),
	Cinnabar			( 826, Textures.SET_ROUGH				,   1.0F,      0,  1, 1    |8                               , 150,   0,   0,   0,	"Cinnabar"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBrown		, 2, Arrays.asList(new MaterialStack(Mercury, 1), new MaterialStack(Sulfur, 1))),
	Clay				( 805, Textures.SET_ROUGH				,   1.0F,      0,  1, 1                                     , 200, 200, 220,   0,	"Clay"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightBlue	, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Lithium, 1), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 2))),
	Coal				( 535, Textures.SET_ROUGH				,   1.0F,      0,  1, 1  |4|8	                            ,  70,  70,  70,   0,	"Coal"							,    0,       0,          0,          0,          0,    0, false, false,   2,   2,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2), new TC_AspectStack(TC_Aspects.IGNIS, 2))),
	Cobaltite			( 827, Textures.SET_METALLIC			,   1.0F,      0,  1, 1    |8                               ,  80,  80, 250,   0,	"Cobaltite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(Cobalt, 1), new MaterialStack(Arsenic, 1), new MaterialStack(Sulfur, 1))),
	Cooperite			( 828, Textures.SET_METALLIC			,   1.0F,      0,  1, 1    |8                               , 255, 255, 200,   0,	"Sheldonite"					,    0,       0,          0,          0,          0,    0, false, false,   5,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Platinum, 3), new MaterialStack(Nickel, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Palladium, 1))),
	Cupronickel			( 310, Textures.SET_METALLIC			,   6.0F,     64,  1, 1|2          |64                      , 227, 150, 128,   0,	"Cupronickel"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Nickel, 1))),
	DarkAsh				( 816, Textures.SET_DULL				,   1.0F,      0,  1, 1                                     ,  50,  50,  50,   0,	"Dark Ashes"					,    0,       0,          0,          0,          0,    0, false, false,   1,   2,   1, Dyes.dyeGray		, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1), new TC_AspectStack(TC_Aspects.PERDITIO, 1))),
	DeepIron			( 829, Textures.SET_METALLIC			,   6.0F,    384,  2, 1|2  |8      |64                      , 150, 140, 140,   0,	"Deep Iron"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyePink		, 2, Arrays.asList(new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))),
	Diamond				( 500, Textures.SET_DIAMOND				,   8.0F,   1280,  3, 1  |4|8      |64|128                  , 200, 255, 255, 127,	"Diamond"						,    0,       0,          0,          0,          0,    0, false,  true,   5, 128,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.LUCRUM, 4))),
	Electrum			( 303, Textures.SET_SHINY				,  12.0F,     64,  2, 1|2          |64|128                  , 255, 255, 100,   0,	"Electrum"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Silver, 1), new MaterialStack(Gold, 1))),
	Emerald				( 501, Textures.SET_EMERALD				,   7.0F,    256,  2, 1  |4|8      |64                      ,  80, 255,  80, 127,	"Emerald"						,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeGreen		, 1, Arrays.asList(new MaterialStack(Beryllium, 3), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 6), new MaterialStack(Oxygen, 18)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.LUCRUM, 5))),
	Galena				( 830, Textures.SET_DULL				,   1.0F,      0,  3, 1    |8                               , 100,  60, 100,   0,	"Galena"						,    0,       0,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyePurple		, 1, Arrays.asList(new MaterialStack(Lead, 3), new MaterialStack(Silver, 3), new MaterialStack(Sulfur, 2))),
	Garnierite			( 906, Textures.SET_METALLIC			,   1.0F,      0,  3, 1    |8                               ,  50, 200,  70,   0,	"Garnierite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightBlue	, 1, Arrays.asList(new MaterialStack(Nickel, 1), new MaterialStack(Oxygen, 1))), 
	Glyceryl			( 714, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            ,   0, 150, 150,   0,	"Glyceryl"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Carbon, 3), new MaterialStack(Hydrogen, 5), new MaterialStack(Nitrogen, 3), new MaterialStack(Oxygen, 9))),
	GreenSapphire		( 504, Textures.SET_GEM_HORIZONTAL		,   7.0F,    256,  2, 1  |4|8      |64                      , 100, 255, 130, 127,	"Green Sapphire"				,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Grossular			( 831, Textures.SET_ROUGH				,   1.0F,      0,  1, 1    |8                               , 200, 100,   0,   0,	"Grossular"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	Ice					( 702, Textures.SET_SHINY				,   1.0F,      0,  0, 1|      16                            , 200, 200, 255,   0,	"Ice"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.GELUM, 2))),
	Ilmenite			( 918, Textures.SET_METALLIC			,   1.0F,      0,  3, 1    |8                               ,  70,  55,  50,   0,	"Ilmenite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   2,   1, Dyes.dyePurple		, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Titanium, 1), new MaterialStack(Oxygen, 3))),
	Invar				( 302, Textures.SET_METALLIC			,   6.0F,    256,  2, 1|2          |64|128                  , 180, 180, 120,   0,	"Invar"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBrown		, 2, Arrays.asList(new MaterialStack(Iron, 2), new MaterialStack(Nickel, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.GELUM, 1))),
	IronCompressed		(  -1, Textures.SET_METALLIC			,   7.0F,     96,  1, 1|2          |64|128                  , 128, 128, 128,   0,	"Compressed Iron"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 2, Arrays.asList(new MaterialStack(Iron, 1))),
	Kanthal				( 312, Textures.SET_METALLIC			,   6.0F,     64,  2, 1|2          |64                      , 194, 210, 223,   0,	"Kanthal"						,    0,       0,          0,          0,       1800, 1800,  true, false,   1,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Chrome, 1))),
	Lazurite			( 524, Textures.SET_LAPIS				,   1.0F,      0,  1, 1  |4|8                               , 100, 120, 255,   0,	"Lazurite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Aluminium, 6), new MaterialStack(Silicon, 6), new MaterialStack(Calcium, 8), new MaterialStack(Sodium, 8))),
	LiveRoot			( 832, Textures.SET_WOOD				,   1.0F,      0,  1, 1                                     , 220, 200,   0,   0,	"Liveroot"						,    5,      16,          0,          0,          0,    0, false, false,   2,   4,   3, Dyes.dyeBrown		, 2, Arrays.asList(new MaterialStack(Wood, 3), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.ARBOR, 2), new TC_AspectStack(TC_Aspects.VICTUS, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1))),
	Magnalium			( 313, Textures.SET_DULL				,   6.0F,    256,  2, 1|2          |64|128                  , 200, 190, 255,   0,	"Magnalium"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightBlue	, 2, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Aluminium, 2))),
	Magnesite			( 908, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 250, 250, 180,   0,	"Magnesite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 3))),
	Magnetite			( 870, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               ,  30,  30,  30,   0,	"Magnetite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 1, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Oxygen, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))),
	Methane				( 715, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            , 255, 255, 255,   0,	"Methane"						,    1,      45,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeMagenta		, 1, Arrays.asList(new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 4))),
	Molybdenite			( 942, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               ,  25,  25,  25,   0,	"Molybdenite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(Molybdenum, 1), new MaterialStack(Sulfur, 2))), // MoS2 (also source of Re)
	Nichrome			( 311, Textures.SET_METALLIC			,   6.0F,     64,  2, 1|2          |64                      , 205, 206, 246,   0,	"Nichrome"						,    0,       0,          0,          0,       2700, 2700,  true, false,   1,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Nickel, 4), new MaterialStack(Chrome, 1))),
	NitroCarbon			( 716, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            ,   0,  75, 100,   0,	"Nitro-Carbon"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Nitrogen, 1), new MaterialStack(Carbon, 1))),
	NitrogenDioxide		( 717, Textures.SET_FLUID				,   1.0F,      0,  1,         16                            , 100, 175, 255,   0,	"Nitrogen Dioxide"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Nitrogen, 1), new MaterialStack(Oxygen, 2))),
	Obsidian			( 804, Textures.SET_DULL				,   1.0F,      0,  3, 1                                     ,  80,  50, 100,   0,	"Obsidian"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Iron, 1), new MaterialStack(Silicon, 2), new MaterialStack(Oxygen, 8))),
	Phosphate			( 833, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8|16                            , 255, 255,   0,   0,	"Phosphate"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeYellow		, 1, Arrays.asList(new MaterialStack(Phosphor, 1), new MaterialStack(Oxygen, 4))),
	PigIron				( 307, Textures.SET_METALLIC			,   6.0F,    384,  2, 1|2  |8      |64                      , 200, 180, 180,   0,	"Pig Iron"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyePink		, 2, Arrays.asList(new MaterialStack(Iron, 1))),
	Plastic				( 874, Textures.SET_DULL				,   3.0F,     32,  1, 1|2          |64|128                  , 200, 200, 200,   0,	"Plastic"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 0, Arrays.asList(new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.MOTUS, 2))),
	Powellite			( 883, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 255, 255,   0,   0,	"Powellite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Molybdenum, 1), new MaterialStack(Oxygen, 4))),
	Pumice				( 926, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 230, 185, 185,   0,	"Pumice"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 2, Arrays.asList(new MaterialStack(Stone, 1))),
	Pyrite				( 834, Textures.SET_ROUGH				,   1.0F,      0,  1, 1    |8                               , 150, 120,  40,   0,	"Pyrite"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Sulfur, 2))),
	Pyrolusite			( 943, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 150, 150, 170,   0,	"Pyrolusite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, 1, Arrays.asList(new MaterialStack(Manganese, 1), new MaterialStack(Oxygen, 2))),
	Pyrope				( 835, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 120,  50, 100,   0,	"Pyrope"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyePurple		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	RockSalt			( 944, Textures.SET_FINE				,   1.0F,      0,  1, 1    |8                               , 240, 200, 200,   0,	"Rock Salt"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Chlorine, 1))),
	Rubber				( 880, Textures.SET_SHINY				,   1.5F,     16,  0, 1|2          |64|128                  ,   0,   0,   0,   0,	"Rubber"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 0, Arrays.asList(new MaterialStack(Carbon, 5), new MaterialStack(Hydrogen, 8)), Arrays.asList(new TC_AspectStack(TC_Aspects.MOTUS, 2))),
	Ruby				( 502, Textures.SET_RUBY				,   7.0F,    256,  2, 1  |4|8      |64                      , 255, 100, 100, 127,	"Ruby"							,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeRed			, 1, Arrays.asList(new MaterialStack(Chrome, 1), new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4))),
	Salt				( 817, Textures.SET_FINE				,   1.0F,      0,  1, 1    |8                               , 250, 250, 250,   0,	"Salt"							,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Chlorine, 1))),
	Saltpeter			( 836, Textures.SET_FINE				,   1.0F,      0,  1, 1    |8                               , 230, 230, 230,   0,	"Saltpeter"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Nitrogen, 1), new MaterialStack(Oxygen, 3))),
	Sapphire			( 503, Textures.SET_GEM_VERTICAL		,   7.0F,    256,  2, 1  |4|8      |64                      , 100, 100, 200, 127,	"Sapphire"						,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Scheelite			( 910, Textures.SET_DULL				,   1.0F,      0,  3, 1    |8                               , 200, 140,  20,   0,	"Scheelite"						,    0,       0,          0,          0,       2500, 2500, false, false,   4,   1,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Tungsten, 1), new MaterialStack(Calcium, 2), new MaterialStack(Oxygen, 4))),
	SiliconDioxide		( 837, Textures.SET_QUARTZ				,   1.0F,      0,  1, 1      |16                            , 255, 255, 255,   0,	"Silicon Dioxide"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLightGray	, 1, Arrays.asList(new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 2))),
	Sodalite			( 525, Textures.SET_LAPIS				,   1.0F,      0,  1, 1  |4|8                               ,  20,  20, 255,   0,	"Sodalite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Sodium, 4), new MaterialStack(Chlorine, 1))),
	SodiumPersulfate	( 718, Textures.SET_FLUID				,   1.0F,      0,  2,         16                            , 255, 255, 255,   0,	"Sodium Persulfate"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4))),
	SodiumSulfide		( 719, Textures.SET_FLUID				,   1.0F,      0,  2,         16                            , 255, 255, 255,   0,	"Sodium Sulfide"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Sulfur, 1))),
	SolderingAlloy		( 314, Textures.SET_DULL				,   1.0F,      0,  1, 1|2                                   , 220, 220, 230,   0,	"Soldering Alloy"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Tin, 9), new MaterialStack(Antimony, 1))),
	Spessartine			( 838, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 255, 100, 100,   0,	"Spessartine"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Manganese, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	Sphalerite			( 839, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 255, 255, 255,   0,	"Sphalerite"					,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeYellow		, 1, Arrays.asList(new MaterialStack(Zinc, 1), new MaterialStack(Sulfur, 1))),
	StainlessSteel		( 306, Textures.SET_SHINY				,   7.0F,    480,  2, 1|2          |64|128                  , 200, 200, 220,   0,	"Stainless Steel"				,    0,       0,          0,          0,       1700, 1700,  true, false,   1,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Iron, 6), new MaterialStack(Chrome, 1), new MaterialStack(Manganese, 1), new MaterialStack(Nickel, 1))),
	Steel				( 305, Textures.SET_METALLIC			,   6.0F,    512,  2, 1|2          |64|128                  , 128, 128, 128,   0,	"Steel"							,    0,       0,          0,          0,       1000, 1000,  true, false,   4,  51,  50, Dyes.dyeGray		, 1, Arrays.asList(new MaterialStack(Iron, 50), new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 1))),
	Stibnite			( 945, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               ,  70,  70,  70,   0,	"Stibnite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Antimony, 2), new MaterialStack(Sulfur, 3))),
	SulfuricAcid		( 720, Textures.SET_FLUID				,   1.0F,      0,  2,         16                            , 255, 128,   0,   0,	"Sulfuric Acid"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4))),
	Tanzanite			( 508, Textures.SET_GEM_VERTICAL		,   7.0F,    256,  2, 1  |4|8      |64                      ,  64,   0, 200, 127,	"Tanzanite"						,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyePurple		, 1, Arrays.asList(new MaterialStack(Calcium, 2), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 13)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Tetrahedrite		( 840, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 200,  32,   0,   0,	"Tetrahedrite"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Copper, 3), new MaterialStack(Antimony, 1), new MaterialStack(Sulfur, 3), new MaterialStack(Iron, 1))), //Cu3SbS3 + x(Fe,Zn)6Sb2S9
	Topaz				( 507, Textures.SET_GEM_HORIZONTAL		,   7.0F,    256,  3, 1  |4|8      |64                      , 255, 128,   0, 127,	"Topaz"							,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeOrange		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Fluorine, 2), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4))),
	Tungstate			( 841, Textures.SET_DULL				,   1.0F,      0,  3, 1    |8                               ,  55,  50,  35,   0,	"Tungstate"						,    0,       0,          0,          0,       2500, 2500,  true, false,   4,   1,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Tungsten, 1), new MaterialStack(Lithium, 2), new MaterialStack(Oxygen, 4))),
	Ultimet				( 344, Textures.SET_SHINY				,   6.0F,    512,  3, 1|2          |64|128                  , 180, 180, 230,   0,	"Ultimet"						,    0,       0,          0,          0,       2700, 2700,  true, false,   1,   1,   1, Dyes.dyeLightBlue	, 1, Arrays.asList(new MaterialStack(Cobalt, 5), new MaterialStack(Chrome, 2), new MaterialStack(Nickel, 1), new MaterialStack(Molybdenum, 1))), // 54% Cobalt, 26% Chromium, 9% Nickel, 5% Molybdenum, 3% Iron, 2% Tungsten, 0.8% Manganese, 0.3% Silicon, 0.08% Nitrogen and 0.06% Carbon
	Uraninite			( 922, Textures.SET_METALLIC			,   1.0F,      0,  3, 1    |8                               ,  35,  35,  35,   0,	"Uraninite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		, 2, Arrays.asList(new MaterialStack(Uranium, 1), new MaterialStack(Oxygen, 2))),
	Uvarovite			( 842, Textures.SET_DIAMOND				,   1.0F,      0,  2, 1    |8                               , 180, 255, 180,   0,	"Uvarovite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Chrome, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12))),
	Water				( 701, Textures.SET_FLUID				,   1.0F,      0,  0,         16                            ,   0,   0, 255,   0,	"Water"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlue		, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2))),
	Wulfenite			( 882, Textures.SET_DULL				,   1.0F,      0,  3, 1    |8                               , 255, 128,   0,   0,	"Wulfenite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Lead, 1), new MaterialStack(Molybdenum, 1), new MaterialStack(Oxygen, 4))),
	WroughtIron			( 304, Textures.SET_METALLIC			,   6.0F,    384,  2, 1|2          |64                      , 200, 180, 180,   0,	"Wrought Iron"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeLightGray	, 2, Arrays.asList(new MaterialStack(Iron, 1))),
	YellowLimonite		( 931, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 200, 200,   0,   0,	"Yellow Limonite"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 2))), // FeO(OH) + a bit Ni and Co
	
	/**
	 * Second Degree Compounds
	 */
	Glass				( 890, Textures.SET_GLASS				,   1.0F,      4,  0, 1  |4                                 , 250, 250, 250, 220,	"Glass"							,    0,       0,          0,          0,          0,    0, false, true ,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2))),
	Perlite				( 925, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               ,  30,  20,  30,   0,	"Perlite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Obsidian, 2), new MaterialStack(Water, 1))),
	Borax				( 941, Textures.SET_FINE				,   1.0F,      0,  1, 1    |8                               , 250, 250, 250,   0,	"Borax"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Boron, 4), new MaterialStack(Water, 10), new MaterialStack(Oxygen, 7))),
	Lignite				( 538, Textures.SET_LIGNITE				,   1.0F,      0,  0, 1  |4|8                               , 100,  70,  70,   0,	"Lignite Coal"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 1, Arrays.asList(new MaterialStack(Carbon, 2), new MaterialStack(Water, 4), new MaterialStack(DarkAsh, 1))),
	Olivine				( 505, Textures.SET_RUBY				,   7.0F,    256,  2, 1  |4|8      |64                      , 150, 255, 150, 127,	"Olivine"						,    0,       0,          0,          0,          0,    0, false,  true,   5,   1,   1, Dyes.dyeLime		, 1, Arrays.asList(new MaterialStack(Magnesium, 2), new MaterialStack(Iron, 1), new MaterialStack(SiliconDioxide, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 2))),
	Opal				( 510, Textures.SET_OPAL				,   7.0F,    256,  2, 1  |4|8      |64                      ,   0,   0, 255,   0,	"Opal"							,    0,       0,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyeBlue		, 1, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Amethyst			( 509, Textures.SET_FLINT				,   7.0F,    256,  3, 1  |4|8      |64                      , 210,  50, 210, 127,	"Amethyst"						,    0,       0,          0,          0,          0,    0, false,  true,   3,   1,   1, Dyes.dyePink		, 1, Arrays.asList(new MaterialStack(SiliconDioxide, 4), new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4))),
	Redstone			( 810, Textures.SET_ROUGH				,   1.0F,      0,  2, 1    |8                               , 200,   0,   0,   0,	"Redstone"						,    0,       0,       5000,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Silicon, 1), new MaterialStack(Pyrite, 5), new MaterialStack(Ruby, 1), new MaterialStack(Mercury, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 2))),
	Lapis				( 526, Textures.SET_LAPIS				,   1.0F,      0,  1, 1  |4|8                               ,  70,  70, 220,   0,	"Lapis"							,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeBlue		, 2, Arrays.asList(new MaterialStack(Lazurite, 12), new MaterialStack(Sodalite, 2), new MaterialStack(Pyrite, 1), new MaterialStack(Calcite, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.SENSUS, 1))),
	Blaze				( 801, Textures.SET_SHINY				,   2.0F,     16,  1, 1            |64                      , 255, 200,   0,   0,	"Blaze"							,    0,       0,          0,          0,          0,    0, false, false,   2,   3,   2, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(DarkAsh, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 2), new TC_AspectStack(TC_Aspects.IGNIS, 4))),
	EnderPearl			( 532, Textures.SET_SHINY				,   1.0F,     16,  1, 1  |4                                 , 108, 220, 200,   0,	"Enderpearl"					,    0,       0,      25000,          0,          0,    0, false, false,   1,  16,  10, Dyes.dyeGreen		, 1, Arrays.asList(new MaterialStack(Beryllium, 1), new MaterialStack(Potassium, 4), new MaterialStack(Nitrogen, 5), new MaterialStack(Magic, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.ALIENIS, 4), new TC_AspectStack(TC_Aspects.ITER, 4), new TC_AspectStack(TC_Aspects.PRAECANTIO, 2))),
	EnderEye			( 533, Textures.SET_SHINY				,   1.0F,     16,  1, 1  |4                                 , 160, 250, 230,   0,	"Endereye"						,    5,      10,      50000,          0,          0,    0, false, false,   1,   2,   1, Dyes.dyeGreen		, 2, Arrays.asList(new MaterialStack(EnderPearl, 1), new MaterialStack(Blaze, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.SENSUS, 4), new TC_AspectStack(TC_Aspects.ALIENIS, 4), new TC_AspectStack(TC_Aspects.ITER, 4), new TC_AspectStack(TC_Aspects.PRAECANTIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 2))),
	Flint				( 802, Textures.SET_FLINT				,   2.5F,     64,  1, 1            |64                      ,   0,  32,  64,   0,	"Flint"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1))),
	Diatomite			( 948, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 225, 225, 225,   0,	"Diatomite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 2, Arrays.asList(new MaterialStack(Flint, 8), new MaterialStack(BandedIron, 1), new MaterialStack(Sapphire, 1))),
	VolcanicAsh			( 940, Textures.SET_FLINT				,   1.0F,      0,  0, 1                                     ,  60,  50,  50,   0,	"Volcanic Ashes"				,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Flint, 6), new MaterialStack(Iron, 1), new MaterialStack(Magnesium, 1))),
	Niter				( 531, Textures.SET_FLINT				,   1.0F,      0,  1, 1  |4|8                               , 255, 200, 200,   0,	"Niter"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		, 2, Arrays.asList(new MaterialStack(Saltpeter, 1))),
	Pyrotheum			( 843, Textures.SET_SHINY				,   1.0F,      0,  1, 1                                     , 255, 128,   0,   0,	"Pyrotheum"						,    2,      62,          0,          0,          0,    0, false, false,   2,   3,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Coal, 1), new MaterialStack(Redstone, 1), new MaterialStack(Blaze, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1))),
	HydratedCoal		( 818, Textures.SET_ROUGH				,   1.0F,      0,  1, 1                                     ,  70,  70, 100,   0,	"Hydrated Coal"					,    0,       0,          0,          0,          0,    0, false, false,   1,   9,   8, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Coal, 8), new MaterialStack(Water, 1))),
	Apatite				( 530, Textures.SET_DIAMOND				,   1.0F,      0,  1, 1  |4|8                               , 200, 200, 255,   0,	"Apatite"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeCyan		, 1, Arrays.asList(new MaterialStack(Calcium, 5), new MaterialStack(Phosphate, 3), new MaterialStack(Chlorine, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.MESSIS, 2))),
	Alumite				(  -1, Textures.SET_METALLIC			,   1.5F,     64,  0, 1|2          |64                      , 255, 255, 255,   0,	"Alumite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		, 2, Arrays.asList(new MaterialStack(Aluminium, 5), new MaterialStack(Iron, 2), new MaterialStack(Obsidian, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.STRONTIO, 2))),
	Manyullyn			(  -1, Textures.SET_METALLIC			,   1.5F,     64,  0, 1|2          |64                      , 255, 255, 255,   0,	"Manyullyn"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		, 2, Arrays.asList(new MaterialStack(Cobalt, 1), new MaterialStack(Aredrite, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.STRONTIO, 2))),
	IronWood			( 338, Textures.SET_WOOD				,   6.0F,    384,  2, 1|2          |64|128                  , 220, 175,   0,   0,	"Ironwood"						,    5,       8,          0,          0,          0,    0, false, false,   2,  19,   9, Dyes.dyeBrown		, 2, Arrays.asList(new MaterialStack(Iron, 9), new MaterialStack(LiveRoot, 9), new MaterialStack(Gold, 1))),
	ShadowIron			( 336, Textures.SET_METALLIC			,   6.0F,    384,  2, 1|2  |8      |64                      , 120, 120, 120,   0,	"Shadowiron"					,    0,       0,          0,          0,          0,    0, false, false,   3,   4,   3, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Magic, 1))),
	ShadowSteel			( 337, Textures.SET_METALLIC			,   6.0F,    768,  2, 1|2          |64                      ,  90,  90,  90,   0,	"Shadowsteel"					,    0,       0,          0,          0,       1700, 1700,  true, false,   4,   4,   3, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Steel, 3), new MaterialStack(Magic, 1))),
	SteelLeaf			( 339, Textures.SET_LEAF				,   8.0F,    768,  3, 1|2          |64                      ,   0, 127,   0,   0,	"Steelleaf"						,    5,      24,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeGreen		, 2, Arrays.asList(new MaterialStack(Steel, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.HERBA, 2), new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1))),
	SterlingSilver		( 350, Textures.SET_SHINY				,  13.0F,    128,  2, 1|2          |64|128                  , 250, 220, 225,   0,	"Sterling Silver"				,    0,       0,          0,          0,       1700, 1700,  true, false,   4,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Silver, 4))),
	RoseGold			( 351, Textures.SET_SHINY				,  14.0F,    128,  2, 1|2          |64|128                  , 255, 230,  30,   0,	"Rose Gold"						,    0,       0,          0,          0,       1600, 1600,  true, false,   4,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Gold, 4))),
	BlackBronze			( 352, Textures.SET_DULL				,  12.0F,    256,  2, 1|2          |64|128                  , 100,  50, 125,   0,	"Black Bronze"					,    0,       0,          0,          0,       2000, 2000,  true, false,   4,   1,   1, Dyes.dyePurple		, 2, Arrays.asList(new MaterialStack(Gold, 1), new MaterialStack(Silver, 1), new MaterialStack(Copper, 3))),
	BismuthBronze		( 353, Textures.SET_DULL				,   8.0F,    256,  2, 1|2          |64|128                  , 100, 125, 125,   0,	"Bismuth Bronze"				,    0,       0,          0,          0,       1100, 1100,  true, false,   4,   1,   1, Dyes.dyeCyan		, 2, Arrays.asList(new MaterialStack(Bismuth, 1), new MaterialStack(Zinc, 1), new MaterialStack(Copper, 3))),
	BlackSteel			( 334, Textures.SET_METALLIC			,   6.5F,    768,  2, 1|2          |64                      , 100, 100, 100,   0,	"Black Steel"					,    0,       0,          0,          0,       1200, 1200,  true, false,   4,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Nickel, 1), new MaterialStack(BlackBronze, 1), new MaterialStack(Steel, 3))),
	RedSteel			( 348, Textures.SET_METALLIC			,   7.0F,    896,  2, 1|2          |64                      , 140, 100, 100,   0,	"Red Steel"						,    0,       0,          0,          0,       1300, 1300,  true, false,   4,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(SterlingSilver, 1), new MaterialStack(BismuthBronze, 1), new MaterialStack(Steel, 2), new MaterialStack(BlackSteel, 4))),
	BlueSteel			( 349, Textures.SET_METALLIC			,   7.5F,   1024,  2, 1|2          |64                      , 100, 100, 140,   0,	"Blue Steel"					,    0,       0,          0,          0,       1400, 1400,  true, false,   4,   1,   1, Dyes.dyeBlue		, 2, Arrays.asList(new MaterialStack(RoseGold, 1), new MaterialStack(Brass, 1), new MaterialStack(Steel, 2), new MaterialStack(BlackSteel, 4))),
	DamascusSteel		( 335, Textures.SET_METALLIC			,   8.0F,   1280,  2, 1|2          |64                      , 110, 110, 110,   0,	"Damascus Steel"				,    0,       0,          0,          0,       1500, 1500,  true, false,   4,   1,   1, Dyes.dyeGray		, 2, Arrays.asList(new MaterialStack(Steel, 1))),
	TungstenSteel		( 316, Textures.SET_METALLIC			,  10.0F,   5120,  4, 1|2          |64|128                  , 100, 100, 160,   0,	"Tungstensteel"					,    0,       0,          0,          0,       3000, 3000,  true, false,   4,   1,   1, Dyes.dyeBlue		, 2, Arrays.asList(new MaterialStack(Steel, 1), new MaterialStack(Tungsten, 1))),
	NitroCoalFuel		( 711, Textures.SET_FLUID				,   1.0F,      0,  2,         16                            ,  50,  70,  50,   0,	"Nitro-Coalfuel"				,    0,      48,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 0, Arrays.asList(new MaterialStack(Glyceryl, 1), new MaterialStack(CoalFuel, 4))),
	NitroFuel			( 709, Textures.SET_FLUID				,   1.0F,      0,  2,         16                            , 200, 255,   0,   0,	"Nitro-Diesel"					,    0,     384,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeLime		, 0, Arrays.asList(new MaterialStack(Glyceryl, 1), new MaterialStack(Fuel, 4))),
	AstralSilver		( 333, Textures.SET_SHINY				,  10.0F,     64,  2, 1|2  |8      |64                      , 230, 230, 255,   0,	"Astral Silver"					,    0,       0,          0,          0,          0,    0, false, false,   4,   3,   2, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Silver, 2), new MaterialStack(Magic, 1))),
	Midasium			( 332, Textures.SET_SHINY				,  12.0F,     64,  2, 1|2  |8      |64                      , 255, 200,  40,   0,	"Midasium"						,    0,       0,          0,          0,          0,    0, false, false,   4,   3,   2, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Gold, 2), new MaterialStack(Magic, 1))),
	Mithril				( 331, Textures.SET_SHINY				,  14.0F,     64,  3, 1|2  |8      |64                      , 255, 255, 210,   0,	"Mithril"						,    0,       0,          0,          0,          0,    0, false, false,   4,   3,   2, Dyes.dyeLightBlue	, 2, Arrays.asList(new MaterialStack(Platinum, 2), new MaterialStack(Magic, 1))),
	BlueAlloy			( 309, Textures.SET_DULL				,   1.0F,      0,  0, 1|2                                   , 100, 180, 255,   0,	"Blue Alloy"					,    0,       0,          0,          0,          0,    0, false, false,   3,   5,   1, Dyes.dyeLightBlue	, 2, Arrays.asList(new MaterialStack(Silver, 1), new MaterialStack(Nikolite, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.ELECTRUM, 3))),
	RedAlloy			( 308, Textures.SET_DULL				,   1.0F,      0,  0, 1|2                                   , 200,   0,   0,   0,	"Red Alloy"						,    0,       0,          0,          0,          0,    0, false, false,   3,   5,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Metal, 1), new MaterialStack(Redstone, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 3))),
	CobaltBrass			( 343, Textures.SET_METALLIC			,   8.0F,    256,  2, 1|2          |64|128                  , 180, 180, 160,   0,	"Cobalt Brass"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(Brass, 7), new MaterialStack(Aluminium, 1), new MaterialStack(Cobalt, 1))),
	Phosphorus			( 534, Textures.SET_FLINT				,   1.0F,      0,  2, 1  |4|8|16                            , 255, 255,   0,   0,	"Phosphorus"					,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Phosphate, 2))),
	Basalt				( 844, Textures.SET_ROUGH				,   1.0F,      0,  1, 1                                     ,  30,  20,  20,   0,	"Basalt"						,    0,       0,          0,          0,          0,    0, false, false,   2,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Olivine, 1), new MaterialStack(Calcite, 3), new MaterialStack(Flint, 8), new MaterialStack(DarkAsh, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.TENEBRAE, 1))),
	GarnetRed			( 527, Textures.SET_RUBY				,   7.0F,    128,  2, 1  |4|8      |64                      , 200,  80,  80, 127,	"Red Garnet"					,    0,       0,          0,          0,          0,    0, false,  true,   4,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Pyrope, 3), new MaterialStack(Almandine, 5), new MaterialStack(Spessartine, 8)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	GarnetYellow		( 528, Textures.SET_RUBY				,   7.0F,    128,  2, 1  |4|8      |64                      , 200, 200,  80, 127,	"Yellow Garnet"					,    0,       0,          0,          0,          0,    0, false,  true,   4,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Andradite, 5), new MaterialStack(Grossular, 8), new MaterialStack(Uvarovite, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3))),
	Marble				( 845, Textures.SET_FINE				,   1.0F,      0,  1, 1                                     , 200, 200, 200,   0,	"Marble"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Calcite, 7)), Arrays.asList(new TC_AspectStack(TC_Aspects.PERFODIO, 1))),
	Sugar				( 803, Textures.SET_FINE				,   1.0F,      0,  1, 1                                     , 250, 250, 250,   0,	"Sugar"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 1, Arrays.asList(new MaterialStack(Carbon, 2), new MaterialStack(Water, 5), new MaterialStack(Oxygen, 25)), Arrays.asList(new TC_AspectStack(TC_Aspects.HERBA, 1), new TC_AspectStack(TC_Aspects.AQUA, 1), new TC_AspectStack(TC_Aspects.AER, 1))),
	Thaumium			( 330, Textures.SET_METALLIC			,  12.0F,    256,  3, 1|2          |64|128                  , 150, 100, 200,   0,	"Thaumium"						,    0,       0,          0,          0,          0,    0, false, false,   5,   2,   1, Dyes.dyePurple		, 0, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1))),
	Vinteum				( 529, Textures.SET_EMERALD				,  10.0F,    128,  3, 1  |4|8      |64                      , 100, 200, 255,   0,	"Vinteum"						,    5,      32,          0,          0,          0,    0, false, false,   4,   1,   1, Dyes.dyeLightBlue	, 2, Arrays.asList(new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1))),
	Vis					(  -1, Textures.SET_SHINY				,   1.0F,      0,  3, 0                                     , 128,   0, 255,   0,	"Vis"							,    5,      32,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePurple		, 2, Arrays.asList(new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.AURAM, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1))),
	Redrock				( 846, Textures.SET_ROUGH				,   1.0F,      0,  1, 1                                     , 255,  80,  50,   0,	"Redrock"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeRed			, 2, Arrays.asList(new MaterialStack(Calcite, 2), new MaterialStack(Flint, 1), new MaterialStack(Clay, 1))),
	PotassiumFeldspar	( 847, Textures.SET_FINE				,   1.0F,      0,  1, 1                                     , 120,  40,  40,   0,	"Potassium Feldspar"			,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyePink		, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 8))),
	Biotite				( 848, Textures.SET_METALLIC			,   1.0F,      0,  1, 1                                     ,  20,  30,  20,   0,	"Biotite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeGray		, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Magnesium, 3), new MaterialStack(Aluminium, 3), new MaterialStack(Fluorine, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 10))),
	GraniteBlack		( 849, Textures.SET_ROUGH				,   4.0F,     64,  3, 1            |64|128                  ,  10,  10,  10,   0,	"Black Granite"					,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 4), new MaterialStack(Biotite, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TUTAMEN, 1))),
	GraniteRed			( 850, Textures.SET_ROUGH				,   4.0F,     64,  3, 1            |64|128                  , 255,   0, 128,   0,	"Red Granite"					,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeMagenta		, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(PotassiumFeldspar, 1), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.TUTAMEN, 1))),
	Chrysotile			( 912, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 110, 140, 110,   0,	"Chrysotile"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(Asbestos, 1))),
	VanadiumMagnetite	( 923, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               ,  35,  35,  60,   0,	"Vanadium Magnetite"			,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(Vanadium, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))), // Mixture of Fe3O4 and V2O5
	BasalticMineralSand	( 935, Textures.SET_SAND				,   1.0F,      0,  1, 1    |8                               ,  40,  50,  40,   0,	"Basaltic Mineral Sand"			,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(Basalt, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))),
	GraniticMineralSand	( 936, Textures.SET_SAND				,   1.0F,      0,  1, 1    |8                               ,  40,  60,  60,   0,	"Granitic Mineral Sand"			,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeBlack		, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(GraniteBlack, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1))),
	GarnetSand			( 938, Textures.SET_SAND				,   1.0F,      0,  1, 1    |8                               , 200, 100,   0,   0,	"Garnet Sand"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeOrange		, 2, Arrays.asList(new MaterialStack(GarnetRed, 1), new MaterialStack(GarnetYellow, 1))),
	QuartzSand			( 939, Textures.SET_SAND				,   1.0F,      0,  1, 1    |8                               , 200, 200, 200,   0,	"Quartz Sand"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes.dyeWhite		, 2, Arrays.asList(new MaterialStack(CertusQuartz, 1), new MaterialStack(Quartzite, 1))),
	Bastnasite			( 905, Textures.SET_FINE				,   1.0F,      0,  2, 1    |8                               , 200, 110,  45,   0,	"Bastnasite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Cerium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Fluorine, 1), new MaterialStack(Oxygen, 3))), // (Ce, La, Y)CO3F
	Pentlandite			( 909, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 165, 150,   5,   0,	"Pentlandite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Nickel, 9), new MaterialStack(Sulfur, 8))), // (Fe,Ni)9S8
	Spodumene			( 920, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 190, 170, 170,   0,	"Spodumene"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Lithium, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Silicon, 2), new MaterialStack(Oxygen, 6))), // LiAl(SiO3)2
	Pollucite			( 919, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 240, 210, 210,   0,	"Pollucite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Caesium, 2), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 4), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 12))), // (Cs,Na)2Al2Si4O12 2H2O (also a source of Rb)
	Tantalite			( 921, Textures.SET_METALLIC			,   1.0F,      0,  3, 1    |8                               , 145,  80,  40,   0,	"Tantalite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Manganese, 1), new MaterialStack(Tantalum, 2), new MaterialStack(Oxygen, 6))), // (Fe, Mn)Ta2O6 (also source of Nb)
	Lepidolite			( 907, Textures.SET_FINE				,   1.0F,      0,  2, 1    |8                               , 240,  50, 140,   0,	"Lepidolite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Lithium, 3), new MaterialStack(Aluminium, 4), new MaterialStack(Fluorine, 2), new MaterialStack(Oxygen, 10))), // K(Li,Al,Rb)3(Al,Si)4O10(F,OH)2
	Glauconite			( 933, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 130, 180,  60,   0,	"Glauconite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Magnesium, 2), new MaterialStack(Aluminium, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12))), // (K,Na)(Fe3+,Al,Mg)2(Si,Al)4O10(OH)2
	Vermiculite			( 932, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 200, 180,  15,   0,	"Vermiculite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Aluminium, 4), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Water, 4), new MaterialStack(Oxygen, 12))), // (Mg+2, Fe+2, Fe+3)3 [(AlSi)4O10] (OH)2 4H2O)
	Bentonite			( 927, Textures.SET_ROUGH				,   1.0F,      0,  2, 1    |8                               , 245, 215, 210,   0,	"Bentonite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Magnesium, 6), new MaterialStack(Silicon, 12), new MaterialStack(Hydrogen, 6), new MaterialStack(Water, 5), new MaterialStack(Oxygen, 36))), // (Na,Ca)0.33(Al,Mg)2(Si4O10)(OH)2 nH2O
	FullersEarth		( 928, Textures.SET_FINE				,   1.0F,      0,  2, 1    |8                               , 160, 160, 120,   0,	"Fullers Earth"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 1), new MaterialStack(Water, 4), new MaterialStack(Oxygen, 11))), // (Mg,Al)2Si4O10(OH) 4(H2O)
	Pitchblende			( 873, Textures.SET_DULL				,   1.0F,      0,  3, 1    |8                               , 200, 210,   0,   0,	"Pitchblende"					,    0,       0,          0,          0,          0,    0, false, false,   5,   1,   1, Dyes.dyeYellow		, 2, Arrays.asList(new MaterialStack(Uraninite, 3), new MaterialStack(Thorium, 1), new MaterialStack(Lead, 1))),
	Malachite			( 871, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               ,   5,  95,   5,   0,	"Malachite"						,    0,       0,          0,          0,          0,    0, false, false,   3,   1,   1, Dyes.dyeGreen		, 1, Arrays.asList(new MaterialStack(Copper, 2), new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 5))), // Cu2CO3(OH)2
	Mirabilite			( 900, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 240, 250, 210,   0,	"Mirabilite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Sulfur, 1), new MaterialStack(Water, 10), new MaterialStack(Oxygen, 4))), // Na2SO4 10H2O
	Mica				( 901, Textures.SET_FINE				,   1.0F,      0,  1, 1    |8                               , 195, 195, 205,   0,	"Mica"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Fluorine, 2), new MaterialStack(Oxygen, 10))), // KAl2(AlSi3O10)(F,OH)2
	Trona				( 903, Textures.SET_METALLIC			,   1.0F,      0,  1, 1    |8                               , 135, 135,  95,   0,	"Trona"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Sodium, 3), new MaterialStack(Carbon, 2), new MaterialStack(Hydrogen, 1), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 6))), // Na3(CO3)(HCO3) 2H2O
	Barite				( 904, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 230, 235, 255,   0,	"Barite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Barium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4))),
	Gypsum				( 934, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               , 230, 230, 250,   0,	"Gypsum"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 4))), // CaSO4 2H2O
	Alunite				( 911, Textures.SET_METALLIC			,   1.0F,      0,  2, 1    |8                               , 225, 180,  65,   0,	"Alunite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 6), new MaterialStack(Oxygen, 14))), // KAl3(SO4)2(OH)6
	Dolomite			( 914, Textures.SET_FLINT				,   1.0F,      0,  1, 1    |8                               , 225, 205, 205,   0,	"Dolomite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Magnesium, 1), new MaterialStack(Carbon, 2), new MaterialStack(Oxygen, 6))), // CaMg(CO3)2
	Wollastonite		( 915, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 240, 240, 240,   0,	"Wollastonite"					,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 3))), // CaSiO3
	Zeolite				( 916, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 240, 230, 230,   0,	"Zeolite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Calcium, 4), new MaterialStack(Silicon, 27), new MaterialStack(Aluminium, 9), new MaterialStack(Water, 28), new MaterialStack(Oxygen, 72))), // NaCa4(Si27Al9)O72 28(H2O)
	Kyanite				( 924, Textures.SET_FLINT				,   1.0F,      0,  2, 1    |8                               , 110, 110, 250,   0,	"Kyanite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 5))), // Al2SiO5
	Kaolinite			( 929, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               , 245, 235, 235,   0,	"Kaolinite"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 4), new MaterialStack(Oxygen, 9))), // Al2Si2O5(OH)4
	Talc				( 902, Textures.SET_DULL				,   1.0F,      0,  2, 1    |8                               ,  90, 180,  90,   0,	"Talc"							,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12))), // H2Mg3(SiO3)4 
	Soapstone			( 877, Textures.SET_DULL				,   1.0F,      0,  1, 1    |8                               ,  95, 145,  95,   0,	"Soapstone"						,    0,       0,          0,          0,          0,    0, false, false,   1,   1,   1, Dyes._NULL			, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12))), // H2Mg3(SiO3)4 
	Concrete			( 947, Textures.SET_ROUGH				,   1.0F,      0,  1, 1                                     , 100, 100, 100,   0,	"Concrete"						,    0,       0,          0,          0,          0,    0, false, false,   0,   1,   1, Dyes.dyeGray		, 0, Arrays.asList(new MaterialStack(Stone, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1))),
	
	/**
	 * Materials which are renamed automatically
	 */
	@Deprecated Palygorskite		(FullersEarth, false),
	@Deprecated Adamantine			(Adamantium, true),
	@Deprecated FzDarkIron			(DarkIron, false),
	@Deprecated FZDarkIron			(DarkIron, false),
	@Deprecated Ashes				(Ash, false),
	@Deprecated DarkAshes			(DarkAsh, false),
	@Deprecated Abyssal				(Basalt, false),
	@Deprecated Adamant				(Adamantium, true),
	@Deprecated AluminumBrass		(AluminiumBrass, false),
	@Deprecated Aluminum			(Aluminium, false),
	@Deprecated NaturalAluminum		(Aluminium, false),
	@Deprecated NaturalAluminium	(Aluminium, false),
	@Deprecated Americum			(Americium, false),
	@Deprecated Beryl				(Emerald, false), // 30,200,200
	@Deprecated BlackGranite		(GraniteBlack, false),
	@Deprecated CalciumCarbonate	(Calcite, false),
	@Deprecated CreosoteOil			(Creosote, false),
	@Deprecated Chromium			(Chrome, false),
	@Deprecated Diesel				(Fuel, false),
	@Deprecated Enderpearl			(EnderPearl, false),
	@Deprecated Endereye			(EnderEye, false),
	@Deprecated EyeOfEnder			(EnderEye, false),
	@Deprecated Eyeofender			(EnderEye, false),
	@Deprecated Flour				(Wheat, false),
	@Deprecated Garnet				(GarnetRed, true),
	@Deprecated Granite				(GraniteBlack, false),
	@Deprecated Kalium				(Potassium, false),
	@Deprecated Lapislazuli			(Lapis, false),
	@Deprecated LapisLazuli			(Lapis, false),
	@Deprecated Monazit				(Monazite, false),
	@Deprecated Natrium				(Sodium, false),
	@Deprecated Mythril				(Mithril, false),
	@Deprecated NitroDiesel			(NitroFuel, false),
	@Deprecated Naquadriah			(Naquadria, false),
	@Deprecated Obby				(Obsidian, false),
	@Deprecated Peridot				(Olivine, true),
	@Deprecated Phosphorite			(Phosphorus, true),
	@Deprecated Quarried			(Marble, false),
	@Deprecated Quicksilver			(Mercury, true),
	@Deprecated QuickSilver			(Mercury, false),
	@Deprecated RedRock				(Redrock, false),
	@Deprecated RefinedIron			(Iron, false),
	@Deprecated RedGranite			(GraniteRed, false),
	@Deprecated Sheldonite			(Cooperite, false),
	@Deprecated Soulsand			(SoulSand, false),
	@Deprecated SilverLead			(Galena, false),
	@Deprecated Titan				(Titanium, false),
	@Deprecated Uran				(Uranium, false),
	@Deprecated Wolframite			(Tungstate, false),
	@Deprecated Wolframium			(Tungsten, false),
	@Deprecated Wolfram				(Tungsten, false),
	@Deprecated WrougtIron			(WroughtIron, false);
	
	/**
	 * List of all Materials.
	 */
	public static final ArrayList<Materials> VALUES = new ArrayList<Materials>(Arrays.asList(values())); 
	
	/**
	 * List of highly Radioactive Materials (enough to get Damage from it).
	 */
	public static final ArrayList<Materials> RADIOACTIVE_MATERIALS = new ArrayList<Materials>();

	/**
	 * List of Elements.
	 */
	public static final ArrayList<Materials> ELEMENTAL_MATERIALS = new ArrayList<Materials>();
	
	static {
		for (Materials tMaterial : VALUES) if (tMaterial.mElement != null && tMaterial.mElement != Element._NULL && !tMaterial.mElement.mIsIsotope) ELEMENTAL_MATERIALS.add(tMaterial);
		
		Wood					.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		WoodSealed				.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Peanutwood				.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		LiveRoot				.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING).add(SubTag.MAGICAL);
		IronWood				.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.MAGICAL);
		SteelLeaf				.add(SubTag.WOOD).add(SubTag.FLAMMABLE).add(SubTag.MAGICAL).add(SubTag.NO_SMELTING);
		
		Ice						.add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Sulfur					.add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Saltpeter				.add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		
		Paper					.add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Coal					.add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Charcoal				.add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Lignite					.add(SubTag.FLAMMABLE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		
		Rubber					.add(SubTag.FLAMMABLE).add(SubTag.NO_SMASHING).add(SubTag.BOUNCY).add(SubTag.STRETCHY);
		Plastic					.add(SubTag.FLAMMABLE).add(SubTag.NO_SMASHING).add(SubTag.BOUNCY);
		
		TNT						.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Gunpowder				.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		Glyceryl				.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		NitroCoalFuel			.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		NitroFuel				.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		NitroCarbon				.add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING).add(SubTag.NO_SMASHING);
		
		ConstructionFoam		.add(SubTag.STONE).add(SubTag.NO_SMASHING).add(SubTag.EXPLOSIVE).add(SubTag.NO_SMELTING);
		Redstone				.add(SubTag.STONE).add(SubTag.NO_SMASHING).add(SubTag.PULVERIZING_CINNABAR);
		Glowstone				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Nikolite				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Stone					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Netherrack				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Brick					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		NetherBrick				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Endstone				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Marble					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Basalt					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Redrock					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Obsidian				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Flint					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		GraniteRed				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		GraniteBlack			.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		Salt					.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		RockSalt				.add(SubTag.STONE).add(SubTag.NO_SMASHING);
		
		Glass					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING);
		Diamond					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Emerald					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Amethyst				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Tanzanite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Topaz					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		BlueTopaz				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Amber					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		GreenSapphire			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Sapphire				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Ruby					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		FoolsRuby				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Opal					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Olivine					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Jasper					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		GarnetRed				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		GarnetYellow			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Lapis					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Apatite					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		NetherQuartz			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		CertusQuartz			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Fluix					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Sodalite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Lazurite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Quartzite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Quartz					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Monazite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Mimichite				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Dilithium				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		CrystalFlux				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Crystal					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Niter					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING);
		Phosphorus				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE);
		Phosphate				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.FLAMMABLE).add(SubTag.EXPLOSIVE);
		InfusedAir				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedFire				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedEarth			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedWater			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedEntropy			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedOrder			.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedVis				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		InfusedDull				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		Vinteum					.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		NetherStar				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		EnderPearl				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		EnderEye				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		Firestone				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		Forcicium				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		Forcillium				.add(SubTag.CRYSTAL).add(SubTag.NO_SMASHING).add(SubTag.NO_SMELTING).add(SubTag.MAGICAL);
		Force					.add(SubTag.CRYSTAL).add(SubTag.MAGICAL);
		Magic					.add(SubTag.CRYSTAL).add(SubTag.MAGICAL);
		
		Blaze					.add(SubTag.MAGICAL).add(SubTag.NO_SMELTING);
		ElvenElementium			.add(SubTag.MAGICAL);
		DarkThaumium			.add(SubTag.MAGICAL);
		Thaumium				.add(SubTag.MAGICAL);
		Enderium				.add(SubTag.MAGICAL);
		AstralSilver			.add(SubTag.MAGICAL);
		Midasium				.add(SubTag.MAGICAL);
		Mithril					.add(SubTag.MAGICAL);
		
		Pyrite					.add(SubTag.BLASTFURNACE_CALCITE_DOUBLE);
		YellowLimonite			.add(SubTag.BLASTFURNACE_CALCITE_DOUBLE);
		BasalticMineralSand		.add(SubTag.BLASTFURNACE_CALCITE_DOUBLE);
		GraniticMineralSand		.add(SubTag.BLASTFURNACE_CALCITE_DOUBLE);
		
		Iron			.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		PigIron			.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		DeepIron		.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		ShadowIron		.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		WroughtIron		.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		MeteoricIron	.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		BrownLimonite	.add(SubTag.BLASTFURNACE_CALCITE_TRIPLE);
		
		Gold			.add(SubTag.WASHING_MERCURY);
		Silver			.add(SubTag.WASHING_MERCURY);
		Osmium			.add(SubTag.WASHING_MERCURY);
		Mithril			.add(SubTag.WASHING_MERCURY);
		Platinum		.add(SubTag.WASHING_MERCURY);
		Midasium		.add(SubTag.WASHING_MERCURY);
		Cooperite		.add(SubTag.WASHING_MERCURY);
		AstralSilver	.add(SubTag.WASHING_MERCURY);
		
		Zinc			.add(SubTag.WASHING_SODIUMPERSULFATE);
		Nickel			.add(SubTag.WASHING_SODIUMPERSULFATE);
		Copper			.add(SubTag.WASHING_SODIUMPERSULFATE);
		Cobalt			.add(SubTag.WASHING_SODIUMPERSULFATE);
		Cobaltite		.add(SubTag.WASHING_SODIUMPERSULFATE);
		Tetrahedrite	.add(SubTag.WASHING_SODIUMPERSULFATE);

		Amber			.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedAir		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedFire		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedEarth	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedWater	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedEntropy	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedOrder	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedVis		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		InfusedDull		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		Salt			.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		RockSalt		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		Scheelite		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		Tungstate		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		Cassiterite		.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		CassiteriteSand	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		NetherQuartz	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		CertusQuartz	.setOreMultiplier( 2).setSmeltingMultiplier( 2);
		Phosphorus		.setOreMultiplier( 3).setSmeltingMultiplier( 3);
		Sulfur			.setOreMultiplier( 4).setSmeltingMultiplier( 4);
		Saltpeter		.setOreMultiplier( 4).setSmeltingMultiplier( 4);
		Apatite			.setOreMultiplier( 4).setSmeltingMultiplier( 4).setByProductMultiplier(2);
		Nikolite		.setOreMultiplier( 5).setSmeltingMultiplier( 5);
		Redstone		.setOreMultiplier( 5).setSmeltingMultiplier( 5);
		Glowstone		.setOreMultiplier( 5).setSmeltingMultiplier( 5);
		Lapis			.setOreMultiplier( 6).setSmeltingMultiplier( 6).setByProductMultiplier(4);
		Sodalite		.setOreMultiplier( 6).setSmeltingMultiplier( 6).setByProductMultiplier(4);
		Lazurite		.setOreMultiplier( 6).setSmeltingMultiplier( 6).setByProductMultiplier(4);
		Monazite		.setOreMultiplier( 8).setSmeltingMultiplier( 8).setByProductMultiplier(2);
		
		Plastic			.setEnchantmentForTools(Enchantment.knockback, 1);
		Rubber			.setEnchantmentForTools(Enchantment.knockback, 2);
		InfusedAir		.setEnchantmentForTools(Enchantment.knockback, 2);
		
		IronWood		.setEnchantmentForTools(Enchantment.fortune, 1);
		SteelLeaf		.setEnchantmentForTools(Enchantment.fortune, 2);
		Midasium		.setEnchantmentForTools(Enchantment.fortune, 2);
		Mithril			.setEnchantmentForTools(Enchantment.fortune, 3);
		Vinteum			.setEnchantmentForTools(Enchantment.fortune, 1);
		Thaumium		.setEnchantmentForTools(Enchantment.fortune, 2);
		DarkThaumium	.setEnchantmentForTools(Enchantment.fortune, 3);
		Magic			.setEnchantmentForTools(Enchantment.fortune, 3);
		InfusedWater	.setEnchantmentForTools(Enchantment.fortune, 3);
		
		Flint			.setEnchantmentForTools(Enchantment.fireAspect, 1);
		DarkIron		.setEnchantmentForTools(Enchantment.fireAspect, 2);
		Firestone		.setEnchantmentForTools(Enchantment.fireAspect, 3);
		FieryBlood		.setEnchantmentForTools(Enchantment.fireAspect, 3);
		Pyrotheum		.setEnchantmentForTools(Enchantment.fireAspect, 3);
		Blaze			.setEnchantmentForTools(Enchantment.fireAspect, 3);
		InfusedFire		.setEnchantmentForTools(Enchantment.fireAspect, 3);
		
		InfusedOrder	.setEnchantmentForTools(Enchantment.silkTouch, 1);
		Force			.setEnchantmentForTools(Enchantment.silkTouch, 1);
		EnderPearl		.setEnchantmentForTools(Enchantment.silkTouch, 1);
		Enderium		.setEnchantmentForTools(Enchantment.silkTouch, 1);
		NetherStar		.setEnchantmentForTools(Enchantment.silkTouch, 1);
		
		BlackBronze		.setEnchantmentForTools(Enchantment.smite, 2);
		Gold			.setEnchantmentForTools(Enchantment.smite, 3);
		RoseGold		.setEnchantmentForTools(Enchantment.smite, 4);
		Platinum		.setEnchantmentForTools(Enchantment.smite, 5);
		InfusedVis		.setEnchantmentForTools(Enchantment.smite, 5);
		
		Lead			.setEnchantmentForTools(Enchantment.baneOfArthropods, 2);
		Nickel			.setEnchantmentForTools(Enchantment.baneOfArthropods, 2);
		Invar			.setEnchantmentForTools(Enchantment.baneOfArthropods, 3);
		Antimony		.setEnchantmentForTools(Enchantment.baneOfArthropods, 3);
		BatteryAlloy	.setEnchantmentForTools(Enchantment.baneOfArthropods, 4);
		Bismuth			.setEnchantmentForTools(Enchantment.baneOfArthropods, 4);
		BismuthBronze	.setEnchantmentForTools(Enchantment.baneOfArthropods, 5);
		
		Iron			.setEnchantmentForTools(Enchantment.sharpness, 1);
		Bronze			.setEnchantmentForTools(Enchantment.sharpness, 1);
		Brass			.setEnchantmentForTools(Enchantment.sharpness, 2);
		Steel			.setEnchantmentForTools(Enchantment.sharpness, 2);
		WroughtIron		.setEnchantmentForTools(Enchantment.sharpness, 2);
		StainlessSteel	.setEnchantmentForTools(Enchantment.sharpness, 3);
		ShadowIron		.setEnchantmentForTools(Enchantment.sharpness, 3);
		ShadowSteel		.setEnchantmentForTools(Enchantment.sharpness, 4);
		BlackSteel		.setEnchantmentForTools(Enchantment.sharpness, 4);
		RedSteel		.setEnchantmentForTools(Enchantment.sharpness, 4);
		BlueSteel		.setEnchantmentForTools(Enchantment.sharpness, 5);
		DamascusSteel	.setEnchantmentForTools(Enchantment.sharpness, 5);
		
		InfusedAir		.setEnchantmentForArmors(Enchantment.respiration, 3);
		
		InfusedFire		.setEnchantmentForArmors(Enchantment.featherFalling, 4);
		
		InfusedEarth	.setEnchantmentForArmors(Enchantment.protection, 4);
		
		InfusedEntropy	.setEnchantmentForArmors(Enchantment.thorns, 3);
		
		InfusedWater	.setEnchantmentForArmors(Enchantment.aquaAffinity, 1);
		
		InfusedOrder	.setEnchantmentForArmors(Enchantment.projectileProtection, 4);
		
		InfusedDull		.setEnchantmentForArmors(Enchantment.blastProtection, 4);
		
		InfusedVis		.setEnchantmentForArmors(Enchantment.protection, 4);
		
		/*
		Stone			.setSpecialEffect(SpecialToolEffect.Crushing, 1);
		GraniteRed		.setSpecialEffect(SpecialToolEffect.Crushing, 2);
		GraniteBlack	.setSpecialEffect(SpecialToolEffect.Crushing, 2);
		Adamantium		.setSpecialEffect(SpecialToolEffect.Crushing, 3);
		
		DeepIron		.setSpecialEffect(SpecialToolEffect.Magnetic, 1);
		MeteoricIron	.setSpecialEffect(SpecialToolEffect.Magnetic, 2);
		MeteoricSteel	.setSpecialEffect(SpecialToolEffect.Magnetic, 3);
		*/
		
		Mercury				.add(SubTag.SMELTING_TO_GEM);
		Cinnabar			.setDirectSmelting(Mercury		).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT).add(SubTag.SMELTING_TO_GEM);
		Celestine			.setDirectSmelting(Strontium	).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Tetrahedrite		.setDirectSmelting(Copper		).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Chalcopyrite		.setDirectSmelting(Copper		).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Malachite			.setDirectSmelting(Copper		).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Pentlandite			.setDirectSmelting(Nickel		).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Sphalerite			.setDirectSmelting(Zinc			).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		Pyrite				.setDirectSmelting(Iron			).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		BasalticMineralSand	.setDirectSmelting(Iron			).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		GraniticMineralSand	.setDirectSmelting(Iron			).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		YellowLimonite		.setDirectSmelting(Iron			).add(SubTag.INDUCTIONSMELTING_LOW_OUTPUT);
		BrownLimonite		.setDirectSmelting(Iron			);
		BandedIron			.setDirectSmelting(Iron			);
		Cassiterite			.setDirectSmelting(Tin			);
		CassiteriteSand		.setDirectSmelting(Tin			);
		Chromite			.setDirectSmelting(Chrome		);
		Garnierite			.setDirectSmelting(Nickel		);
		Cobaltite			.setDirectSmelting(Cobalt		);
		Stibnite			.setDirectSmelting(Antimony		);
		Cooperite			.setDirectSmelting(Platinum		);
		Pyrolusite			.setDirectSmelting(Manganese	);
		Magnesite			.setDirectSmelting(Magnesium	);
		Molybdenite			.setDirectSmelting(Molybdenum	);
		
		Glauconite		.addOreByProduct(Sodium			).addOreByProduct(Aluminium			).addOreByProduct(Iron			);
		Vermiculite		.addOreByProduct(Iron			).addOreByProduct(Aluminium			).addOreByProduct(Magnesium		);
		FullersEarth	.addOreByProduct(Aluminium		).addOreByProduct(Silicon			).addOreByProduct(Magnesium		);
		Bentonite		.addOreByProduct(Aluminium		).addOreByProduct(Calcium			).addOreByProduct(Magnesium		);
		Bastnasite		.addOreByProduct(Yttrium		).addOreByProduct(Lanthanum			).addOreByProduct(Cerium		);
		Uraninite		.addOreByProduct(Uranium		).addOreByProduct(Thorium			).addOreByProduct(Plutonium		);
		Pitchblende		.addOreByProduct(Thorium		).addOreByProduct(Uranium			).addOreByProduct(Lead			);
		Galena			.addOreByProduct(Sulfur			).addOreByProduct(Silver			).addOreByProduct(Lead			);
		Lapis			.addOreByProduct(Lazurite		).addOreByProduct(Sodalite			).addOreByProduct(Pyrite		);
		Pyrite			.addOreByProduct(Sulfur			).addOreByProduct(Phosphorus		).addOreByProduct(Iron			);
		Copper			.addOreByProduct(Cobalt			).addOreByProduct(Gold				).addOreByProduct(Nickel		);
		Nickel			.addOreByProduct(Cobalt			).addOreByProduct(Platinum			).addOreByProduct(Iron			);
		GarnetRed		.addOreByProduct(Spessartine	).addOreByProduct(Pyrope			).addOreByProduct(Almandine		);
		GarnetYellow	.addOreByProduct(Andradite		).addOreByProduct(Grossular			).addOreByProduct(Uvarovite		);
		Cooperite		.addOreByProduct(Palladium		).addOreByProduct(Nickel			).addOreByProduct(Iridium		);
		Cinnabar		.addOreByProduct(Redstone		).addOreByProduct(Sulfur			).addOreByProduct(Glowstone		);
		Tantalite		.addOreByProduct(Manganese		).addOreByProduct(Niobium			).addOreByProduct(Tantalum		);
		Pollucite		.addOreByProduct(Caesium		).addOreByProduct(Aluminium			).addOreByProduct(Rubidium		);
		Chrysotile		.addOreByProduct(Asbestos		).addOreByProduct(Silicon			).addOreByProduct(Magnesium		);
		Asbestos		.addOreByProduct(Asbestos		).addOreByProduct(Silicon			).addOreByProduct(Magnesium		);
		Sphalerite		.addOreByProduct(Zinc			).addOreByProduct(GarnetYellow		).addOreByProduct(Cadmium		);
		Chalcopyrite	.addOreByProduct(Pyrite			).addOreByProduct(Cobalt			).addOreByProduct(Cadmium		); // Gold?
		Pentlandite		.addOreByProduct(Iron			).addOreByProduct(Sulfur			).addOreByProduct(Cobalt		);
		Uranium			.addOreByProduct(Lead			).addOreByProduct(Plutonium			).addOreByProduct(Thorium		);
		Scheelite		.addOreByProduct(Manganese		).addOreByProduct(Molybdenum		).addOreByProduct(Calcium		);
		Tungstate		.addOreByProduct(Manganese		).addOreByProduct(Silver			).addOreByProduct(Lithium		);
		Tungsten		.addOreByProduct(Manganese		).addOreByProduct(Molybdenum		);
		Diatomite		.addOreByProduct(BandedIron		).addOreByProduct(Sapphire			);
		Iron			.addOreByProduct(Nickel			).addOreByProduct(Tin				);
		Lepidolite		.addOreByProduct(Lithium		).addOreByProduct(Caesium			);
		Gold			.addOreByProduct(Copper			).addOreByProduct(Nickel			);
		Tin				.addOreByProduct(Iron			).addOreByProduct(Zinc				);
		Antimony		.addOreByProduct(Zinc			).addOreByProduct(Iron				);
		Silver			.addOreByProduct(Lead			).addOreByProduct(Sulfur			);
		Lead			.addOreByProduct(Silver			).addOreByProduct(Sulfur			);
		Thorium			.addOreByProduct(Uranium		).addOreByProduct(Lead				);
		Plutonium		.addOreByProduct(Uranium		).addOreByProduct(Lead				);
		Electrum		.addOreByProduct(Gold			).addOreByProduct(Silver			);
		Bronze			.addOreByProduct(Copper			).addOreByProduct(Tin				);
		Brass			.addOreByProduct(Copper			).addOreByProduct(Zinc				);
		Coal			.addOreByProduct(Lignite		).addOreByProduct(Thorium			);
		Redstone		.addOreByProduct(Cinnabar		).addOreByProduct(Glowstone			);
		Glowstone		.addOreByProduct(Redstone		).addOreByProduct(Gold				);
		Ilmenite		.addOreByProduct(Iron			).addOreByProduct(Titanium			);
		Bauxite			.addOreByProduct(Grossular		).addOreByProduct(Titanium			);
		Manganese		.addOreByProduct(Chrome			).addOreByProduct(Iron				);
		Sapphire		.addOreByProduct(Aluminium		).addOreByProduct(GreenSapphire		);
		GreenSapphire	.addOreByProduct(Aluminium		).addOreByProduct(Sapphire			);
		Platinum		.addOreByProduct(Nickel			).addOreByProduct(Iridium			);
		Emerald			.addOreByProduct(Beryllium		).addOreByProduct(Aluminium			);
		Olivine			.addOreByProduct(Pyrope			).addOreByProduct(Magnesium			);
		Chrome			.addOreByProduct(Iron			).addOreByProduct(Magnesium			);
		Chromite		.addOreByProduct(Iron			).addOreByProduct(Magnesium			);
		Tetrahedrite	.addOreByProduct(Antimony		).addOreByProduct(Zinc				);
		QuartzSand		.addOreByProduct(CertusQuartz	).addOreByProduct(Quartzite			);
		GarnetSand		.addOreByProduct(GarnetRed		).addOreByProduct(GarnetYellow		);
		Magnetite		.addOreByProduct(Iron			).addOreByProduct(Gold				);
		GraniticMineralSand.addOreByProduct(GraniteBlack).addOreByProduct(Magnetite			);
		BasalticMineralSand.addOreByProduct(Basalt		).addOreByProduct(Magnetite			);
		Basalt			.addOreByProduct(Olivine		).addOreByProduct(DarkAsh			);
		Celestine		.addOreByProduct(Strontium		).addOreByProduct(Sulfur			);
		VanadiumMagnetite.addOreByProduct(Magnetite		).addOreByProduct(Vanadium			);
		Lazurite		.addOreByProduct(Sodalite		).addOreByProduct(Lapis				);
		Sodalite		.addOreByProduct(Lazurite		).addOreByProduct(Lapis				);
		Spodumene		.addOreByProduct(Aluminium		).addOreByProduct(Lithium			);
		Ruby			.addOreByProduct(Chrome			).addOreByProduct(GarnetRed			);
		Phosphorus		.addOreByProduct(Apatite		).addOreByProduct(Phosphate			);
		Iridium			.addOreByProduct(Platinum		).addOreByProduct(Osmium			);
		Pyrope			.addOreByProduct(GarnetRed		).addOreByProduct(Magnesium			);
		Almandine		.addOreByProduct(GarnetRed		).addOreByProduct(Aluminium			);
		Spessartine		.addOreByProduct(GarnetRed		).addOreByProduct(Manganese			);
		Andradite		.addOreByProduct(GarnetYellow	).addOreByProduct(Iron				);
		Grossular		.addOreByProduct(GarnetYellow	).addOreByProduct(Calcium			);
		Uvarovite		.addOreByProduct(GarnetYellow	).addOreByProduct(Chrome			);
		YellowLimonite	.addOreByProduct(Nickel			).addOreByProduct(Cobalt			);
		NaquadahEnriched.addOreByProduct(Naquadah		).addOreByProduct(Naquadria			);
		Naquadah		.addOreByProduct(NaquadahEnriched);
		BrownLimonite	.addOreByProduct(YellowLimonite	);
		Pyrolusite		.addOreByProduct(Manganese		);
		Molybdenite		.addOreByProduct(Molybdenum		);
		Stibnite		.addOreByProduct(Antimony		);
		Garnierite		.addOreByProduct(Nickel			);
		Lignite			.addOreByProduct(Coal			);
		Diamond			.addOreByProduct(Graphite		);
		Beryllium		.addOreByProduct(Emerald		);
		Forcicium		.addOreByProduct(Thorium		);
		Forcillium		.addOreByProduct(Thorium		);
		Monazite		.addOreByProduct(Thorium		);
		Quartzite		.addOreByProduct(CertusQuartz	);
		CertusQuartz	.addOreByProduct(Quartzite		);
		Calcite			.addOreByProduct(Andradite		);
		Apatite			.addOreByProduct(Phosphorus		);
		Zinc			.addOreByProduct(Tin			);
		Nikolite		.addOreByProduct(Diamond		);
		Magnesite		.addOreByProduct(Magnesium		);
		NetherQuartz	.addOreByProduct(Netherrack		);
		PigIron			.addOreByProduct(Iron			);
		DeepIron		.addOreByProduct(Iron			);
		ShadowIron		.addOreByProduct(Iron			);
		DarkIron		.addOreByProduct(Iron			);
		MeteoricIron	.addOreByProduct(Iron			);
		Steel			.addOreByProduct(Iron			);
		Mithril			.addOreByProduct(Platinum		);
		Midasium		.addOreByProduct(Gold			);
		AstralSilver	.addOreByProduct(Silver			);
		Graphite		.addOreByProduct(Carbon			);
		Netherrack		.addOreByProduct(Sulfur			);
		Flint			.addOreByProduct(Obsidian		);
		Cobaltite		.addOreByProduct(Cobalt			);
		Cobalt			.addOreByProduct(Cobaltite		);
		Sulfur			.addOreByProduct(Sulfur			);
		Saltpeter		.addOreByProduct(Saltpeter		);
		Endstone		.addOreByProduct(Helium_3		);
		Osmium			.addOreByProduct(Iridium		);
		Magnesium		.addOreByProduct(Olivine		);
		Aluminium		.addOreByProduct(Bauxite		);
		Titanium		.addOreByProduct(Almandine		);
		Obsidian		.addOreByProduct(Olivine		);
		Ash				.addOreByProduct(Carbon			);
		DarkAsh			.addOreByProduct(Carbon			);
		Redrock			.addOreByProduct(Clay			);
		Marble			.addOreByProduct(Calcite		);
		Clay			.addOreByProduct(Clay			);
		Cassiterite		.addOreByProduct(Tin			);
		CassiteriteSand	.addOreByProduct(Tin			);
		GraniteBlack	.addOreByProduct(Biotite		);
		GraniteRed		.addOreByProduct(PotassiumFeldspar);
		Phosphate		.addOreByProduct(Phosphor		);
		Phosphor		.addOreByProduct(Phosphate		);
		Jade			.addOreByProduct(Jade			);
		Tanzanite		.addOreByProduct(Opal			);
		Opal			.addOreByProduct(Tanzanite		);
		Amethyst		.addOreByProduct(Amethyst		);
		Jasper			.addOreByProduct(FoolsRuby		);
		FoolsRuby		.addOreByProduct(Jasper			);
		Amber			.addOreByProduct(Amber			);
		Topaz			.addOreByProduct(BlueTopaz		);
		BlueTopaz		.addOreByProduct(Topaz			);
		Niter			.addOreByProduct(Saltpeter		);
		Vinteum			.addOreByProduct(Vinteum		);
		Force			.addOreByProduct(Force			);
		Dilithium		.addOreByProduct(Dilithium		);
		Neutronium		.addOreByProduct(Neutronium		);
		Lithium			.addOreByProduct(Lithium		);
		Silicon			.addOreByProduct(SiliconDioxide	);
		Salt			.addOreByProduct(RockSalt		);
		RockSalt		.addOreByProduct(Salt			);
		
		Materials.UUAmplifier.mChemicalFormula = "Accelerates the Mass Fabricator";
		FoolsRuby.mChemicalFormula = Ruby.mChemicalFormula;
		NaquadahEnriched.mChemicalFormula = "Nq+";
		Naquadah.mChemicalFormula = "Nq";
		Naquadria.mChemicalFormula = "NqX";
	}
	
	public static Materials get(String aMaterialName) {
		Object tObject = GT_Utility.getFieldContent(Materials.class, aMaterialName, false, false);
		if (tObject != null && tObject instanceof Materials) return (Materials)tObject;
		return _NULL;
	}
	
	public static Materials getRealMaterial(String aMaterialName) {
		return get(aMaterialName).mMaterialInto;
	}
	
	/**
	 * Called in preInit with the Config to set Values.
	 * @param aConfiguration
	 */
	public static void init(GT_Config aConfiguration) {
		for (Materials tMaterial : VALUES) {
			String tString = tMaterial.toString().toLowerCase();
			if (tMaterial.mBlastFurnaceRequired) tMaterial.mBlastFurnaceRequired = aConfiguration.get(ConfigCategories.Materials.blastfurnacerequirements, tString, true);
			if (tMaterial.mAmplificationValue > 0) tMaterial.mAmplificationValue = aConfiguration.get(ConfigCategories.Materials.UUM_MaterialCost, tString, tMaterial.mAmplificationValue);
			if (tMaterial.mUUMEnergy > 0) tMaterial.mUUMEnergy = aConfiguration.get(ConfigCategories.Materials.UUM_EnergyCost, tString, tMaterial.mUUMEnergy);
			if (tMaterial.mBlastFurnaceRequired && aConfiguration.get(ConfigCategories.Materials.blastinductionsmelter, tString, tMaterial.mBlastFurnaceTemp < 1500)) GT_ModHandler.ThermalExpansion.addSmelterBlastOre(tMaterial);
		}
	}
	
	public boolean isRadioactive() {
		if (mElement != null) return mElement.mHalfLifeSeconds >= 0;
		for (MaterialStack tMaterial : mMaterialList) if (tMaterial.mMaterial.isRadioactive()) return true;
		return false;
	}
	
	public long getProtons() {
		if (mElement != null) return mElement.getProtons();
		if (mMaterialList.size() <= 0) return Element.Tc.getProtons();
		long rAmount = 0, tAmount = 0;
		for (MaterialStack tMaterial : mMaterialList) {
			tAmount += tMaterial.mAmount;
			rAmount += tMaterial.mAmount * tMaterial.mMaterial.getProtons();
		}
		return (getDensity() * rAmount) / (tAmount * GregTech_API.MATERIAL_UNIT);
	}
	
	public long getNeutrons() {
		if (mElement != null) return mElement.getNeutrons();
		if (mMaterialList.size() <= 0) return Element.Tc.getNeutrons();
		long rAmount = 0, tAmount = 0;
		for (MaterialStack tMaterial : mMaterialList) {
			tAmount += tMaterial.mAmount;
			rAmount += tMaterial.mAmount * tMaterial.mMaterial.getNeutrons();
		}
		return (getDensity() * rAmount) / (tAmount * GregTech_API.MATERIAL_UNIT);
	}
	
	public long getMass() {
		if (mElement != null) return mElement.getMass();
		if (mMaterialList.size() <= 0) return Element.Tc.getMass();
		long rAmount = 0, tAmount = 0;
		for (MaterialStack tMaterial : mMaterialList) {
			tAmount += tMaterial.mAmount;
			rAmount += tMaterial.mAmount * tMaterial.mMaterial.getMass();
		}
		return (getDensity() * rAmount) / (tAmount * GregTech_API.MATERIAL_UNIT);
	}
	
	public long getDensity() {
		return mDensity;
	}
	
	public String getToolTip() {
		return getToolTip(1, false);
	}
	
	public String getToolTip(boolean aShowQuestionMarks) {
		return getToolTip(1, aShowQuestionMarks);
	}
	
	public String getToolTip(long aMultiplier) {
		return getToolTip(aMultiplier, false);
	}
	
	public String getToolTip(long aMultiplier, boolean aShowQuestionMarks) {
		if (!aShowQuestionMarks && mChemicalFormula.equals("?")) return "";
		if (getDensity() * aMultiplier >= GregTech_API.MATERIAL_UNIT * 2 && !mMaterialList.isEmpty()) {
			return ((mElement != null || (mMaterialList.size() < 2 && mMaterialList.get(0).mAmount == 1))?mChemicalFormula:"(" + mChemicalFormula + ")") + ((getDensity() * aMultiplier) / GregTech_API.MATERIAL_UNIT);
		}
		return mChemicalFormula;
	}
	
	private final ArrayList<ItemStack> mMaterialItems = new ArrayList<ItemStack>();
	
	/**
	 * Adds an ItemStack to this Material.
	 */
	public Materials add(ItemStack aStack) {
		if (aStack != null && !contains(aStack)) mMaterialItems.add(aStack);
		return this;
	}
	
	/**
	 * This is used to determine if any of the ItemStacks belongs to this Material.
	 */
	public boolean contains(ItemStack... aStacks) {
		if (aStacks == null || aStacks.length <= 0) return false;
		for (ItemStack tStack : mMaterialItems) for (ItemStack aStack : aStacks) if (GT_Utility.areStacksEqual(aStack, tStack, !tStack.hasTagCompound())) return true;
		return false;
	}
	
	/**
	 * This is used to determine if an ItemStack belongs to this Material.
	 */
	public boolean remove(ItemStack aStack) {
		if (aStack == null) return false;
		boolean temp = false;
		for (int i = 0; i < mMaterialItems.size(); i++) if (GT_Utility.areStacksEqual(aStack, mMaterialItems.get(i))) {
			mMaterialItems.remove(i--);
			temp = true;
		}
		return temp;
	}
	
	private final List<SubTag> mSubTags = new ArrayList<SubTag>();
	
	/**
	 * Adds a SubTag to this Material
	 */
	public Materials add(SubTag aTag) {
		if (aTag != null && !contains(aTag)) mSubTags.add(aTag);
		return this;
	}
	
	/**
	 * If this Material has this exact SubTag
	 */
	public boolean contains(SubTag aTag) {
		return mSubTags.contains(aTag);
	}
	
	/**
	 * Removes a SubTag from this Material
	 */
	public boolean remove(SubTag aTag) {
		return mSubTags.remove(aTag);
	}
	
	/**
	 * Adds a Material to the List of Byproducts when grinding this Ore.
	 * Is used for more precise Ore grinding, so that it is possible to choose between certain kinds of Materials.
	 */
	public Materials addOreByProduct(Materials aMaterial) {
		if (!mOreByProducts.contains(aMaterial.mMaterialInto)) mOreByProducts.add(aMaterial.mMaterialInto);
		return this;
	}
	
	/**
	 * If this Ore gives multiple drops of its Main Material.
	 * Lapis Ore for example gives about 6 drops.
	 */
	public Materials setOreMultiplier(int aOreMultiplier) {
		if (aOreMultiplier > 0) mOreMultiplier = aOreMultiplier;
		return this;
	}
	
	/**
	 * If this Ore gives multiple drops of its Byproduct Material.
	 */
	public Materials setByProductMultiplier(int aByProductMultiplier) {
		if (aByProductMultiplier > 0) mByProductMultiplier = aByProductMultiplier;
		return this;
	}
	
	/**
	 * If this Ore gives multiple drops of its Main Material.
	 * Lapis Ore for example gives about 6 drops.
	 */
	public Materials setSmeltingMultiplier(int aSmeltingMultiplier) {
		if (aSmeltingMultiplier > 0) mSmeltingMultiplier = aSmeltingMultiplier;
		return this;
	}
	
	/**
	 * This Ore should be smolten directly into an Ingot of this Material instead of an Ingot of itself.
	 */
	public Materials setDirectSmelting(Materials aMaterial) {
		if (aMaterial != null) mDirectSmelting = aMaterial.mMaterialInto;
		return this;
	}

	/**
	 * This Material should be the Main Material this Ore gets ground into.
	 * Example, Chromite giving Chrome or Tungstate giving Tungsten.
	 */
	public Materials setOreReplacement(Materials aMaterial) {
		if (aMaterial != null) mOreReplacement = aMaterial.mMaterialInto;
		return this;
	}
	
	public Materials setEnchantmentForTools(Enchantment aEnchantment, int aEnchantmentLevel) {
		mEnchantmentTools = aEnchantment;
		mEnchantmentToolsLevel = (byte)aEnchantmentLevel;
		if (aEnchantment instanceof Enchantment_Radioactivity) RADIOACTIVE_MATERIALS.add(this);
		return this;
	}
	
	public Materials setEnchantmentForArmors(Enchantment aEnchantment, int aEnchantmentLevel) {
		mEnchantmentArmors = aEnchantment;
		mEnchantmentArmorsLevel = (byte)aEnchantmentLevel;
		if (aEnchantment instanceof Enchantment_Radioactivity) RADIOACTIVE_MATERIALS.add(this);
		return this;
	}
	
	/**
	 * This Array can be changed dynamically by a Tick Handler in order to get a glowing Effect on all GT Meta Items out of this Material.
	 */
	public final short[] mRGBa = new short[] {255, 255, 255, 0};
	
	public Enchantment mEnchantmentTools = null, mEnchantmentArmors = null;
	public byte mEnchantmentToolsLevel = 0, mEnchantmentArmorsLevel = 0;
	public final IIconContainer[] mIconSet;
	public boolean mBlastFurnaceRequired = false, mTransparent = false;
	public float mToolSpeed = 1.0F;
	public String mChemicalFormula = "?", mDefaultLocalName = "null";
	public Dyes mColor = Dyes._NULL;
	public short mMeltingPoint = 0, mBlastFurnaceTemp = 0;
	public int mTypes = 0, mDurability = 16, mAmplificationValue = 0, mUUMEnergy = 0, mFuelPower = 0, mFuelType = 0, mExtraData = 0, mOreValue = 0, mOreMultiplier = 1, mByProductMultiplier = 1, mSmeltingMultiplier = 1;
	public long mDensity = GregTech_API.MATERIAL_UNIT;
	public Element mElement = null;
	public Materials mDirectSmelting = this, mOreReplacement = this;
	public byte mToolQuality = 0;
	public final int mMetaItemSubID;
	public final boolean mUnificatable;
	public final Materials mMaterialInto;
	public final List<MaterialStack> mMaterialList = new ArrayList<MaterialStack>();
	public final List<Materials> mOreByProducts = new ArrayList<Materials>(), mOreReRegistrations = new ArrayList<Materials>();
	public final List<TC_AspectStack> mAspects = new ArrayList<TC_AspectStack>();
	public Fluid mSolid = null, mFluid = null, mGas = null, mPlasma = null;
	/** This Fluid is used as standard Unit for Molten Materials. 1296 is a Molten Block, what means 144 is one Material Unit worth */
	public Fluid mStandardMoltenFluid = null;
	
	public FluidStack getSolid(long aAmount) {
		if (mSolid == null) return null;
		return new FluidStack(mSolid, (int)aAmount);
	}
	
	public FluidStack getFluid(long aAmount) {
		if (mFluid == null) return null;
		return new FluidStack(mFluid, (int)aAmount);
	}
	
	public FluidStack getGas(long aAmount) {
		if (mGas == null) return null;
		return new FluidStack(mGas, (int)aAmount);
	}
	
	public FluidStack getPlasma(long aAmount) {
		if (mPlasma == null) return null;
		return new FluidStack(mPlasma, (int)aAmount);
	}
	
	public FluidStack getMolten(long aAmount) {
		if (mStandardMoltenFluid == null) return null;
		return new FluidStack(mStandardMoltenFluid, (int)aAmount);
	}
	
	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, boolean aUnificatable) {
		mUnificatable = aUnificatable;
		mMaterialInto = this;
		mMetaItemSubID = aMetaItemSubID;
		mToolQuality = (byte)aToolQuality;
		mDurability = aToolDurability;
		mToolSpeed = aToolSpeed;
		mIconSet = Arrays.copyOf(aIconSet, 128);
		if (aMetaItemSubID >= 0) {
			if (GregTech_API.sGeneratedMaterials[aMetaItemSubID] == null) {
				GregTech_API.sGeneratedMaterials[aMetaItemSubID] = this;
			} else {
				throw new IllegalArgumentException("The Index " + aMetaItemSubID + " is already used!");
			}
		}
	}
	
	private Materials(Materials aMaterialInto, boolean aReRegisterIntoThis) {
		mUnificatable = false;
		mDefaultLocalName = aMaterialInto.mDefaultLocalName;
		mMaterialInto = aMaterialInto.mMaterialInto;
		if (aReRegisterIntoThis) mMaterialInto.mOreReRegistrations.add(this);
		mChemicalFormula = aMaterialInto.mChemicalFormula;
		mMetaItemSubID = -1;
		mIconSet = Textures.SET_NONE;
	}
	
	/**
	 * @param aMetaItemSubID the Sub-ID used in my own MetaItems. Range 0-1000. -1 for no Material
	 * @param aTypes which kind of Items should be generated. Bitmask as follows:
	 *      1 = Dusts of all kinds.
	 *      2 = Dusts, Ingots, Plates, Rods/Sticks, Machine Components and other Metal specific things.
	 *      4 = Dusts, Gems, Plates, Lenses (if transparent).
	 *      8 = Dusts, Impure Dusts, crushed Ores, purified Ores, centrifuged Ores etc.
	 *     16 = Cells
	 *     32 = Plasma Cells
	 *     64 = Tool Heads
	 *    128 = Gears
	 * @param aR, aG, aB Color of the Material 0-255 each.
	 * @param aA transparency of the Material Texture. 0 = fully visible, 255 = Invisible.
	 * @param aLocalName The Name used as Default for localization.
	 * @param aFuelType Type of Generator to get Energy from this Material.
	 * @param aFuelPower EU generated. Will be multiplied by 1000, also additionally multiplied by 2 for Gems.
	 * @param aAmplificationValue Amount of UUM amplifier gotten from this.
	 * @param aUUMEnergy Amount of EU needed to shape the UUM into this Material.
	 * @param aMeltingPoint Used to determine the smelting Costs in Furnii.
	 * @param aBlastFurnaceTemp Used to determine the needed Heat capactiy Costs in Blast Furnii.
	 * @param aBlastFurnaceRequired If this requires a Blast Furnace.
	 * @param aColor Vanilla MC Wool Color which comes the closest to this.
	 */
	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, int aTypes, int aR, int aG, int aB, int aA, String aLocalName, int aFuelType, int aFuelPower, int aAmplificationValue, int aUUMEnergy, int aMeltingPoint, int aBlastFurnaceTemp, boolean aBlastFurnaceRequired, boolean aTransparent, int aOreValue, int aDensityMultiplier, int aDensityDivider, Dyes aColor) {
		this(aMetaItemSubID, aIconSet, aToolSpeed, aToolDurability, aToolQuality, true);
		mDefaultLocalName = aLocalName;
		mMeltingPoint = (short)aMeltingPoint;
		mBlastFurnaceTemp = (short)aBlastFurnaceTemp;
		mBlastFurnaceRequired = aBlastFurnaceRequired;
		mTransparent = aTransparent;
		mAmplificationValue = aAmplificationValue;
		mUUMEnergy = aUUMEnergy;
		mFuelPower = aFuelPower;
		mFuelType = aFuelType;
		mOreValue = aOreValue;
		mDensity = (GregTech_API.MATERIAL_UNIT * aDensityMultiplier) / aDensityDivider;
		mColor = aColor==null?Dyes._NULL:aColor;
		mRGBa[0] = (short)aR;
		mRGBa[1] = (short)aG;
		mRGBa[2] = (short)aB;
		mRGBa[3] = (short)aA;
		mTypes = aTypes;
	}

	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, int aTypes, int aR, int aG, int aB, int aA, String aLocalName, int aFuelType, int aFuelPower, int aAmplificationValue, int aUUMEnergy, int aMeltingPoint, int aBlastFurnaceTemp, boolean aBlastFurnaceRequired, boolean aTransparent, int aOreValue, int aDensityMultiplier, int aDensityDivider, Dyes aColor, List<TC_AspectStack> aAspects) {
		this(aMetaItemSubID, aIconSet, aToolSpeed, aToolDurability, aToolQuality, aTypes, aR, aG, aB, aA, aLocalName, aFuelType, aFuelPower, aAmplificationValue, aUUMEnergy, aMeltingPoint, aBlastFurnaceTemp, aBlastFurnaceRequired, aTransparent, aOreValue, aDensityMultiplier, aDensityDivider, aColor);
		mAspects.addAll(aAspects);
	}
	
	/**
	 * @param aElement The Element Enum represented by this Material
	 */
	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, int aTypes, int aR, int aG, int aB, int aA, String aLocalName, int aFuelType, int aFuelPower, int aAmplificationValue, int aUUMEnergy, int aMeltingPoint, int aBlastFurnaceTemp, boolean aBlastFurnaceRequired, boolean aTransparent, int aOreValue, int aDensityMultiplier, int aDensityDivider, Dyes aColor, Element aElement, List<TC_AspectStack> aAspects) {
		this(aMetaItemSubID, aIconSet, aToolSpeed, aToolDurability, aToolQuality, aTypes, aR, aG, aB, aA, aLocalName, aFuelType, aFuelPower, aAmplificationValue, aUUMEnergy, aMeltingPoint, aBlastFurnaceTemp, aBlastFurnaceRequired, aTransparent, aOreValue, aDensityMultiplier, aDensityDivider, aColor);
		mElement = aElement;
		mElement.mLinkedMaterials.add(this);
		if (aElement == Element._NULL) {
			mChemicalFormula = "Empty";
		} else {
			mChemicalFormula = aElement.toString();
			mChemicalFormula = mChemicalFormula.replaceAll("_", "-");
		}
		mAspects.addAll(aAspects);
	}
	
	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, int aTypes, int aR, int aG, int aB, int aA, String aLocalName, int aFuelType, int aFuelPower, int aAmplificationValue, int aUUMEnergy, int aMeltingPoint, int aBlastFurnaceTemp, boolean aBlastFurnaceRequired, boolean aTransparent, int aOreValue, int aDensityMultiplier, int aDensityDivider, Dyes aColor, int aExtraData, List<MaterialStack> aMaterialList) {
		this(aMetaItemSubID, aIconSet, aToolSpeed, aToolDurability, aToolQuality, aTypes, aR, aG, aB, aA, aLocalName, aFuelType, aFuelPower, aAmplificationValue, aUUMEnergy, aMeltingPoint, aBlastFurnaceTemp, aBlastFurnaceRequired, aTransparent, aOreValue, aDensityMultiplier, aDensityDivider, aColor);
		mExtraData = aExtraData;
		mMaterialList.addAll(aMaterialList);
		mChemicalFormula = "";
		for (MaterialStack tMaterial : mMaterialList) {
			mChemicalFormula += tMaterial.toString();
		}
		mChemicalFormula = mChemicalFormula.replaceAll("_", "-");
		
		
		int tAmountOfComponents = 0;
		for (MaterialStack tMaterial : mMaterialList) {
			tAmountOfComponents += tMaterial.mAmount;
			for (TC_AspectStack tAspect : tMaterial.mMaterial.mAspects) tAspect.addToAspectList(mAspects);
		}
		tAmountOfComponents *= aDensityMultiplier;
		tAmountOfComponents /= aDensityDivider;
		for (TC_AspectStack tAspect : mAspects) {
			tAspect.mAmount = Math.max(1, tAspect.mAmount / tAmountOfComponents);
		}
	}
	
	private Materials(int aMetaItemSubID, IIconContainer[] aIconSet, float aToolSpeed, int aToolDurability, int aToolQuality, int aTypes, int aR, int aG, int aB, int aA, String aLocalName, int aFuelType, int aFuelPower, int aAmplificationValue, int aUUMEnergy, int aMeltingPoint, int aBlastFurnaceTemp, boolean aBlastFurnaceRequired, boolean aTransparent, int aOreValue, int aDensityMultiplier, int aDensityDivider, Dyes aColor, int aExtraData, List<MaterialStack> aMaterialList, List<TC_AspectStack> aAspects) {
		this(aMetaItemSubID, aIconSet, aToolSpeed, aToolDurability, aToolQuality, aTypes, aR, aG, aB, aA, aLocalName, aFuelType, aFuelPower, aAmplificationValue, aUUMEnergy, aMeltingPoint, aBlastFurnaceTemp, aBlastFurnaceRequired, aTransparent, aOreValue, aDensityMultiplier, aDensityDivider, aColor);
		mExtraData = aExtraData;
		mMaterialList.addAll(aMaterialList);
		mChemicalFormula = "";
		for (MaterialStack tMaterial : mMaterialList) {
			mChemicalFormula += tMaterial.toString();
		}
		mChemicalFormula = mChemicalFormula.replaceAll("_", "-");
		mAspects.addAll(aAspects);
	}
	
	public static volatile int VERSION = 503;

	@Override
	public short[] getRGBA() {
		return mRGBa;
	}
}