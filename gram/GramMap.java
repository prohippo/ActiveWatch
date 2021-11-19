// Copyright (c) 2021, C. P. Mah
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
//   Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// -----------------------------------------------------------------------------
// AW file GramMap.java : 16nov2021 CPM
// lookup for 3-, 4-, and 5-grams

package gram;

import java.util.*;

public class GramMap {

	private static final String[] l4g = {
		"aban", "abit", "able", "acce", "acci", "acco", "ache", "acre", "adem", "adet",
		"admi", "aero", "agen", "ague", "aign", "aile", "aint", "aire", "aise", "alan",
		"alco", "alle", "allo", "ally", "ambl", "amer", "amin", "amou", "ampl", "anal",
		"anar", "anat", "ance", "anch", "andi", "andl", "ange", "anon", "anso", "anta",
		"anti", "anyo", "apar", "appe", "appl", "appo", "appr", "apse", "aqua", "arab",
		"aran", "arch", "area", "arge", "arma", "arni", "arra", "arre", "arri", "arro",
		"arry", "arse", "arth", "arti", "asia", "asis", "ason", "assa", "assi", "assu",
		"aste", "astr", "asur", "atar", "atch", "ater", "atin", "atio", "atri", "atta",
		"atte", "atto", "audi", "ault", "aunt", "ause", "aust", "auth", "avai", "aven",
		"avid", "awar", "back", "bact", "bait", "bala", "ball", "band", "bank", "barr",
		"base", "bate", "batt", "bear", "beat", "bell", "bene", "berg", "best", "bibl",
		"bill", "bjec", "blea", "blem", "bloo", "blue", "body", "bomb", "bone", "book",
		"born", "bott", "boul", "bran", "brav", "brid", "brit", "broa", "brow", "bull",
		"bump", "burg", "bush", "bute", "butt", "cade", "cala", "calc", "cale", "cali",
		"call", "camp", "cana", "canc", "cand", "capa", "cape", "capt", "card", "care",
		"carr", "cart", "case", "cast", "cate", "caus", "ceiv", "cele", "cell", "cent",
		"cept", "cere", "cern", "cert", "cess", "chal", "cham", "chan", "char", "chas",
		"chea", "chem", "chet", "chic", "chie", "chin", "chis", "choo", "chor", "chri",
		"chro", "chur", "cide", "cien", "city", "civi", "cket", "ckle", "clar", "clim",
		"clin", "clos", "club", "coat", "cold", "coll", "colo", "colu", "comb", "come",
		"comm", "comp", "conc", "cond", "cone", "conf", "cong", "conn", "cons", "cont",
		"conv", "cope", "cord", "core", "corn", "corp", "corr", "cost", "cote", "cott",
		"coun", "cour", "cove", "crat", "crea", "cred", "crep", "cret", "crew", "crit",
		"crow", "crum", "cryp", "ctic", "cula", "cult", "curr", "cuse", "cuss", "cust",
		"cute", "cyte", "cyto", "dale", "dana", "danc", "dang", "dard", "dare", "dash",
		"data", "date", "daug", "ddle", "dead", "deal", "dean", "dear", "deat", "deca",
		"dece", "deci", "decl", "deep", "defe", "defi", "dela", "dele", "deli", "demo",
		"dens", "dent", "depe", "dera", "desc", "desi", "desp", "dest", "dete", "deve",
		"devi", "dict", "dida", "dier", "diff", "digi", "disa", "disc", "dish", "disp",
		"dist", "dium", "divi", "dona", "done", "doom", "door", "dote", "down", "doze",
		"draw", "drea", "dres", "driv", "drop", "drum", "duce", "dump", "dust", "each",
		"eant", "eard", "earm", "earn", "eart", "ease", "easo", "east", "eath", "eave",
		"ebat", "ebra", "ebri", "ecal", "echo", "ecia", "ecla", "ecom", "econ", "ecor",
		"ecre", "ectr", "eden", "eder", "edes", "edge", "edit", "educ", "eech", "eeti",
		"eeze", "efea", "egen", "egot", "eigh", "eleb", "elec", "eleg", "elem", "eles",
		"elig", "elim", "elin", "elit", "elon", "elte", "elve", "emai", "eman", "embe",
		"embl", "embr", "emed", "emem", "emen", "emer", "emis", "emon", "emor", "empl",
		"empt", "enal", "enam", "ench", "enef", "enfo", "enor", "ense", "ensu", "ente",
		"enti", "entr", "eopl", "epar", "epor", "epre", "eput", "equa", "eque", "equi",
		"erag", "eral", "erap", "erit", "erro", "erse", "erso", "erty", "erve", "esca",
		"esig", "esis", "esti", "estr", "etai", "etal", "etch", "eter", "etro", "etto",
		"eval", "evel", "even", "ever", "evic", "evid", "evil", "exam", "exce", "expe",
		"expl", "exte", "extr", "face", "fact", "fail", "fair", "fall", "fare", "farm",
		"fast", "fate", "favo", "feat", "fect", "feel", "fess", "fest", "ffer", "ffle",
		"ffor", "fice", "fict", "fiel", "file", "fill", "film", "fina", "find", "fini",
		"fire", "firm", "fish", "floo", "flor", "flow", "fold", "folk", "foll", "food",
		"foot", "forc", "ford", "fore", "forg", "form", "fort", "frag", "free", "fres",
		"fric", "fter", "full", "func", "fund", "fuse", "gain", "gall", "game", "gard",
		"gate", "genc", "gend", "gene", "gent", "germ", "gest", "giga", "gine", "girl",
		"give", "glan", "glen", "glob", "gold", "good", "gote", "grad", "gram", "grap",
		"grat", "grav", "gray", "gree", "grou", "grow", "grum", "guar", "gull", "gust",
		"hair", "half", "hall", "hame", "hamp", "hanc", "hand", "hank", "hant", "happ",
		"hard", "harm", "hart", "hase", "hast", "hate", "have", "head", "heal", "hear",
		"heat", "heav", "heel", "heir", "hell", "help", "hema", "hemi", "hemo", "hend",
		"herd", "hero", "hest", "hexa", "hick", "hief", "high", "hill", "hind", "hine",
		"hing", "hint", "hist", "hman", "hold", "hole", "home", "hone", "hool", "hope",
		"hore", "hori", "host", "hour", "hous", "hull", "hump", "hydr", "hyst", "ibut",
		"ican", "icat", "icer", "icip", "idea", "idge", "ield", "ient", "ieve", "igat",
		"igen", "iger", "ight", "igni", "ilet", "imen", "imet", "imit", "imme", "impa",
		"impl", "impo", "impr", "inap", "inch", "inci", "inco", "inde", "indu", "inet",
		"infl", "info", "inge", "inse", "insp", "inst", "insu", "inta", "inte", "inti",
		"intr", "inve", "invo", "ipse", "iron", "isla", "isle", "ispo", "issu", "iste",
		"istr", "itch", "iter", "itio", "itis", "itut", "ivat", "iver", "ivil", "izen",
		"jail", "japa", "ject", "jest", "join", "jump", "just", "keep", "kick", "kill",
		"kilo", "kind", "king", "know", "labo", "lace", "lack", "lade", "lage", "laim",
		"land", "lane", "lang", "lant", "lare", "larg", "lari", "lash", "lass", "last",
		"late", "lati", "lead", "lean", "lear", "leas", "lebr", "lect", "leep", "lega",
		"lend", "lent", "less", "lete", "lett", "lice", "lick", "lict", "life", "lige",
		"ligi", "limb", "limi", "lind", "line", "ling", "lint", "lion", "list", "lite",
		"lith", "live", "llab", "llet", "lley", "llow", "loca", "lock", "logi", "lone",
		"long", "look", "lore", "loss", "love", "lter", "lude", "lump", "lust", "lute",
		"mage", "main", "majo", "make", "mala", "male", "mall", "manc", "mand", "mang",
		"mani", "mank", "mans", "mant", "manu", "mari", "mark", "marr", "mart", "mary",
		"mass", "mast", "mate", "math", "matt", "mber", "mbin", "mble", "mean", "meas",
		"meat", "mech", "meda", "medi", "meet", "mega", "melt", "memo", "mend", "mens",
		"ment", "mera", "merc", "merg", "mess", "mete", "meth", "metr", "mile", "mili",
		"mill", "mind", "mine", "mini", "mint", "minu", "miss", "mist", "mmer", "mmis",
		"mmit", "mode", "mold", "mond", "mone", "mont", "moor", "more", "morn", "mort",
		"mote", "moti", "moun", "move", "movi", "mpac", "mpar", "mpas", "mpat", "mper",
		"mple", "muni", "musi", "myst", "nage", "name", "nate", "nati", "natu", "ncia",
		"ndam", "nder", "ndic", "ndle", "neck", "nect", "need", "nega", "nerg", "nest",
		"netw", "neut", "ngag", "nger", "ngin", "ngle", "ngra", "ngth", "nick", "nion",
		"nior", "nkle", "nman", "nnel", "noon", "norm", "note", "noti", "nser", "nsio",
		"nsis", "nsit", "nsti", "nstr", "nten", "nter", "ntin", "ntry", "nute", "oach",
		"oard", "oast", "obab", "ocke", "octo", "ocus", "odel", "odge", "offe", "offi",
		"ogra", "oice", "olat", "ollo", "olve", "oman", "ombi", "omen", "omet", "omma",
		"ompa", "ompe", "ompl", "onar", "onat", "onde", "ondu", "onen", "oney", "ongr",
		"onic", "onom", "onor", "onsc", "onst", "ontr", "ooth", "open", "oper", "opin",
		"ople", "opti", "orge", "orig", "orit", "orri", "orth", "ortu", "osit", "ossi",
		"otel", "othe", "oubl", "ouch", "ough", "ounc", "ound", "oung", "ount", "ourn",
		"ouse", "oust", "oute", "outh", "over", "ower", "oyal", "pace", "pack", "pact",
		"pain", "pair", "pale", "pane", "pani", "pant", "para", "pare", "pari", "park",
		"parl", "part", "pass", "past", "pate", "path", "patr", "patt", "peak", "pect",
		"pena", "pend", "peni", "pent", "peop", "perc", "perf", "peri", "perm", "pers",
		"pert", "pest", "pete", "peti", "phil", "phys", "pick", "pict", "pike", "pine",
		"plac", "plan", "plas", "plat", "play", "plet", "plex", "plic", "ploy", "poin",
		"pois", "poke", "pole", "poli", "pond", "pone", "pore", "port", "pose", "posi",
		"post", "ppea", "ppen", "ppli", "prea", "prec", "pred", "prem", "prep", "pres",
		"pret", "prev", "pric", "prim", "prin", "prio", "pris", "priv", "prob", "proc",
		"prof", "prom", "prop", "pros", "prot", "prov", "publ", "pull", "pump", "push",
		"pute", "quad", "quar", "quen", "ques", "quir", "quit", "race", "rack", "ract",
		"rade", "radi", "raft", "rage", "rail", "rain", "rald", "rama", "rame", "ramp",
		"ranc", "rand", "rang", "rank", "rans", "rash", "rass", "rast", "rate", "rati",
		"rato", "rcen", "reac", "read", "reak", "real", "ream", "reap", "rear", "reas",
		"reat", "rebe", "rebo", "reca", "rece", "reck", "reco", "rect", "rede", "redi",
		"redu", "reed", "reet", "refe", "refo", "refu", "regi", "regu", "rela", "rele",
		"reli", "reme", "remo", "rend", "rent", "repe", "repo", "repr", "repu", "resc",
		"resh", "resi", "reso", "resp", "ress", "rest", "resu", "reta", "retr", "reve",
		"rgen", "rget", "ribe", "rice", "rich", "rick", "rict", "ride", "rike", "rina",
		"rine", "ring", "ript", "rise", "rish", "risk", "rita", "rive", "rize", "rman",
		"rmor", "rnet", "road", "robe", "robo", "rock", "rode", "roid", "roke", "role",
		"roll", "roma", "romo", "rone", "rong", "roni", "ront", "rook", "room", "rope",
		"rose", "roun", "roup", "rous", "rout", "rove", "rran", "rres", "rrow", "rtun",
		"rude", "rule", "rump", "rush", "rust", "sacr", "safe", "sage", "sala", "sale",
		"sali", "salo", "sand", "sane", "sani", "sano", "sati", "scal", "scan", "scar",
		"scen", "sche", "scho", "scor", "scot", "scou", "scri", "scur", "scus", "sear",
		"seas", "seat", "sect", "secu", "sele", "self", "sell", "semi", "send", "sens",
		"sent", "sequ", "seri", "sers", "serv", "sess", "seve", "shad", "sham", "shan",
		"shap", "shar", "shea", "shel", "ship", "shir", "shoo", "shop", "shor", "shot",
		"show", "sick", "side", "sign", "sist", "size", "slea", "slow", "sman", "snap",
		"soci", "soft", "sole", "solu", "some", "sore", "sort", "sour", "spac", "spar",
		"spec", "spee", "spel", "spir", "spit", "spla", "spon", "spot", "spri", "squa",
		"ssen", "sset", "stab", "staf", "stag", "stak", "stal", "stan", "star", "stat",
		"stay", "stel", "sten", "step", "ster", "stic", "stig", "stit", "stle", "stoc",
		"ston", "stop", "stor", "stra", "stre", "stri", "stro", "stru", "stud", "subs",
		"succ", "suit", "sult", "sume", "summ", "sump", "supp", "sure", "surg", "surv",
		"susp", "synt", "tabl", "tack", "tact", "tail", "tain", "take", "tale", "tali",
		"tall", "tamp", "tang", "tard", "targ", "tate", "team", "tech", "tect", "teen",
		"tele", "tell", "temp", "tena", "tenc", "tend", "teno", "tens", "tent", "terc",
		"terf", "term", "terr", "terv", "test", "text", "thar", "ther", "thin", "thol",
		"thon", "thor", "thre", "thro", "tick", "ticl", "tige", "tilt", "time", "tina",
		"tine", "ting", "tinu", "tion", "tire", "tiss", "tman", "tole", "tomb", "tomo",
		"tone", "tono", "toon", "tore", "tort", "tory", "tour", "tout", "town", "trac",
		"trad", "trai", "tran", "trap", "trav", "trea", "tree", "trem", "tren", "tres",
		"tria", "trib", "tric", "trip", "trol", "tron", "trou", "troy", "truc", "trum",
		"trus", "tter", "ttle", "tude", "ture", "turn", "tute", "type", "uard", "uate",
		"ubli", "udge", "uenc", "uest", "ught", "uite", "ulat", "ulti", "umbl", "unce",
		"unch", "uncl", "unct", "unde", "unic", "unio", "unit", "urch", "uret", "urge",
		"urse", "ussi", "ustr", "utch", "valu", "vant", "veal", "vect", "vehi", "velo",
		"vene", "vent", "verh", "vers", "vert", "vest", "vice", "vict", "vide", "view",
		"vill", "vinc", "vine", "viol", "visi", "vite", "vive", "void", "voke", "volu",
		"vote", "walk", "wall", "want", "ward", "ware", "warm", "warn", "warr", "wash",
		"wast", "weal", "wear", "week", "well", "west", "whis", "whol", "wick", "wife",
		"wind", "wine", "wing", "word", "work", "worl", "writ", "xper", "xplo", "yard",
		"yarn", "year", "yoff", "youn", "yout", "zero", "zest", "zone", "zoom", "zzle"
	};

	private static final String[] l5g = {
		"actic", "actor", "advan", "agree", "allow", "ament", "ameri", "anger", "anima", "arden",
		"assoc", "ation", "attle", "austr", "avail", "award", "battl", "beach", "black", "board",
		"bread", "break", "broad", "brook", "broth", "brown", "build", "busin", "ceive", "centr",
		"centu", "chanc", "chang", "chara", "charg", "chase", "chief", "child", "circl", "circu",
		"claim", "class", "clear", "cliff", "cline", "clude", "colon", "commo", "compa", "compe",
		"confe", "confi", "conne", "const", "consu", "conta", "conte", "contr", "conve", "counc",
		"count", "cours", "court", "cover", "creat", "cross", "crown", "curre", "custo", "decid",
		"defen", "democ", "depar", "depen", "desig", "devel", "direc", "disco", "distr", "dress",
		"drive", "dynam", "ealth", "earin", "earth", "eason", "easur", "educa", "egion", "egree",
		"eight", "elect", "ellow", "emand", "ember", "emplo", "energ", "enter", "eport", "equen",
		"ermin", "escri", "estab", "estig", "estim", "event", "expec", "exten", "famil", "fathe",
		"featu", "field", "fight", "finan", "flavo", "force", "fores", "frame", "frien", "front",
		"fruit", "futur", "gener", "globe", "gover", "grain", "grand", "grant", "graph", "green",
		"gress", "groun", "grove", "hance", "happe", "haven", "heart", "heath", "highw", "histo",
		"house", "human", "icipa", "iddle", "ident", "imate", "impor", "indus", "iness", "infor",
		"insta", "instr", "integ", "inter", "intro", "inves", "islan", "isten", "large", "learn",
		"level", "light", "limit", "litic", "llage", "llege", "lleng", "llion", "locat", "macro",
		"manag", "manor", "march", "marke", "maste", "matio", "meado", "media", "membe", "merge",
		"micro", "might", "milit", "milli", "minat", "minut", "mmuni", "model", "money", "month",
		"motor", "mount", "nance", "natio", "natur", "neral", "ngine", "night", "nsist", "ocean",
		"offic", "ology", "ommun", "ontra", "opera", "order", "organ", "otent", "other", "ouble",
		"ounce", "paign", "paper", "paren", "peopl", "perio", "phase", "phone", "photo", "pital",
		"place", "plain", "plane", "plant", "plent", "plete", "plica", "point", "polic", "polit",
		"posit", "power", "ppear", "pport", "pract", "press", "price", "princ", "pring", "prise",
		"proba", "proce", "produ", "progr", "proje", "prope", "prote", "prove", "publi", "quart",
		"quest", "quick", "radio", "raise", "ranch", "range", "reach", "recom", "recor", "reduc",
		"refer", "refor", "regul", "rench", "repla", "repor", "repub", "resid", "ridge", "right",
		"river", "roduc", "round", "scape", "schoo", "scien", "score", "searc", "seaso", "secur",
		"senat", "sense", "share", "short", "sight", "simil", "sland", "slate", "smart", "socia",
		"sound", "sourc", "space", "speak", "spect", "spend", "spite", "splay", "sport", "sprin",
		"squar", "ssist", "stabl", "staff", "stake", "stall", "stand", "start", "state", "stati",
		"stead", "steal", "steam", "steel", "still", "stock", "stone", "store", "storm", "story",
		"strai", "strat", "stree", "struc", "style", "super", "table", "teach", "techn", "tempt",
		"teria", "thank", "theat", "theor", "therm", "think", "tight", "tower", "track", "tract",
		"trade", "train", "trans", "trave", "treat", "trial", "trict", "trong", "truct", "ublic",
		"ultur", "ument", "under", "unive", "ustle", "valle", "velop", "venue", "veter", "villa",
		"ville", "volve", "watch", "water", "white", "winte", "woman", "world", "write", "ystem"
	};

	private HashMap<String,Integer> h4g; // for 4-gram lookup
	private HashMap<String,Integer> h5g; //     5-gram

	public GramMap ( ) {
		h4g = new HashMap<String,Integer>();
		for (int i = 0; i < l4g.length; i++)
			h4g.put(l4g[i],i);
		h5g = new HashMap<String,Integer>();
		for (int i = 0; i < l5g.length; i++)
			h5g.put(l5g[i],i);
	}

	public String decode4g ( int x ) { return (x < l4g.length) ? l4g[x] : ""; }

	public String decode5g ( int x ) { return (x < l5g.length) ? l5g[x] : ""; }

	public int encode4g ( String s ) { return (h4g.containsKey(s)) ? h4g.get(s) : -1; }

	public int encode5g ( String s ) { return (h5g.containsKey(s)) ? h5g.get(s) : -1; }

	public int count4g ( ) { return l4g.length; }

	public int count5g ( ) { return l5g.length; }

	public static void main ( String[] av ) {
		GramMap gm = new GramMap();
		System.out.println(gm.count4g() + " 4gs, " + gm.count5g() + " 5gs");
		String[] a = { "1" , "2" , "4" , "8" , "16" , "writ" , "world" , "xray" , "battle" };
		if (av.length > 0) a = av;
		for (int i = 0; i < a.length; i++) {
			String x = a[i]; 
			System.out.print(x);
			if (x.length() == 0)
				System.out.println(" (null)");
			else if (Character.isDigit(x.charAt(0))) {
				int k = Integer.parseInt(x);
				System.out.println(": 4g= [" + gm.decode4g(k) + "]");
				System.out.print(x);
				System.out.println(": 5g= [" + gm.decode5g(k) + "]");
			}
			else if (x.length() == 4)
				System.out.println(": 4g =" + gm.encode4g(x));
			else if (x.length() == 5)
				System.out.println(": 5g =" + gm.encode5g(x));
			else
				System.out.println(": unrecognized");
		}
	}
}
