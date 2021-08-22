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
// AW file GramMap.java : 16aug2021 CPM
// lookup for longer n-grams

package gram;

import java.util.*;

public class GramMap {

	private static final String[] l4g = {
		"able", "acce", "ache", "admi", "ague", "aint", "aise", "allo", "ally", "amou",
		"ampl", "anal", "ance", "anch", "ange", "anta", "anti", "appe", "appl", "appr",
		"aran", "arch", "area", "arge", "arri", "arry", "arth", "arti", "ason", "aste",
		"astr", "asur", "atch", "ater", "atin", "atio", "atta", "atte", "ause", "aust",
		"auth", "avai", "avai", "back", "ball", "band", "bank", "base", "batt", "beat",
		"bill", "bjec", "blem", "body", "book", "born", "born", "bran", "butt", "calc",
		"calc", "call", "camp", "cana", "cand", "card", "care", "case", "cast", "caus",
		"ceiv", "cele", "cell", "cell", "cent", "cept", "cert", "cess", "chal", "cham",
		"chan", "char", "chas", "chas", "chis", "choo", "chri", "cide", "cien", "city",
		"clos", "club", "coll", "colo", "comb", "come", "comm", "comp", "conc", "cond",
		"conf", "conn", "cons", "cont", "conv", "cord", "core", "corp", "cost", "coun",
		"cour", "cove", "crat", "crea", "cred", "crit", "crit", "cula", "cult", "curr",
		"data", "date", "deal", "deci", "defe", "defi", "defi", "demo", "dent", "dera",
		"desi", "dest", "dete", "dete", "diff", "disa", "disc", "disp", "dist", "divi",
		"down", "drea", "dres", "driv", "each", "earn", "eart", "ease", "easo", "east",
		"eath", "eave", "ecia", "econ", "ecor", "ecre", "ectr", "edge", "edit", "educ",
		"efea", "eigh", "elec", "elig", "elve", "embe", "emer", "empl", "enam", "ente",
		"enti", "entr", "epar", "epor", "epre", "equa", "eque", "equi", "eral", "erro",
		"erse", "erty", "erve", "esca", "esis", "esti", "estr", "etai", "eter", "evel",
		"even", "ever", "evic", "exam", "exce", "expe", "exte", "extr", "face", "fact",
		"fail", "fair", "fall", "fast", "feat", "fect", "fess", "ffer", "ffor", "fiel",
		"film", "fina", "fini", "fire", "foll", "food", "foot", "forc", "fore", "forg",
		"form", "fort", "free", "fter", "gain", "game", "gate", "genc", "gene", "gent",
		"germ", "gest", "give", "gold", "good", "grad", "gram", "gree", "grou", "grow",
		"guar", "hair", "hair", "half", "hamp", "hanc", "hand", "happ", "hard", "hast",
		"head", "heal", "hear", "heat", "heel", "heir", "help", "herd", "hero", "herr",
		"high", "hine", "hist", "hold", "hole", "home", "hone", "host", "icat", "icip",
		"idea", "idge", "igat", "ight", "igni", "impl", "impo", "impr", "indu", "infl",
		"infl", "inge", "inse", "insp", "inst", "inte", "inti", "inve", "invo", "iron",
		"iron", "isla", "ispo", "iste", "istr", "itch", "itio", "itis", "iver", "ject",
		"just", "keep", "kill", "kind", "king", "know", "labo", "lace", "lack", "laim",
		"land", "lane", "lane", "lang", "lant", "last", "late", "lati", "lead", "lean",
		"lear", "leas", "lect", "lega", "lete", "lice", "life", "line", "lion", "list",
		"live", "llow", "loca", "lock", "logi", "long", "look", "loss", "love", "lter",
		"lude", "mage", "main", "majo", "make", "mand", "mani", "mark", "mart", "mart",
		"mate", "mber", "mean", "meas", "meda", "medi", "meet", "memo", "ment", "mess",
		"mess", "meth", "mili", "mind", "mind", "mine", "mini", "miss", "mmer", "mmit",
		"mode", "mont", "more", "moun", "move", "movi", "mper", "mple", "muni", "musi",
		"nage", "name", "nati", "natu", "nder", "ndle", "need", "ngle", "note", "nsti",
		"nstr", "nter", "ntin", "oach", "oard", "octo", "offe", "offi", "ogra", "oice",
		"oice", "olve", "omen", "omma", "ompl", "onar", "onde", "oney", "onom", "onst",
		"ontr", "open", "oper", "opin", "opti", "orge", "orig", "orit", "orth", "ossi",
		"othe", "oubl", "ouch", "ough", "ounc", "ound", "oung", "ount", "ourn", "ouse",
		"outh", "over", "pace", "pain", "pani", "para", "pare", "park", "part", "pass",
		"past", "peak", "pect", "pend", "perf", "peri", "pers", "pert", "phil", "pick",
		"plac", "plan", "play", "plet", "poin", "poli", "port", "pose", "posi", "post",
		"ppen", "ppli", "pres", "prim", "prin", "pris", "prob", "proc", "prof", "prom",
		"prop", "prot", "prov", "quar", "quen", "ques", "quir", "race", "rack", "ract",
		"rade", "radi", "raft", "rage", "rail", "rail", "ranc", "rand", "rang", "rank",
		"rate", "rati", "rato", "read", "real", "rear", "reas", "reca", "rece", "reco",
		"rect", "rect", "redi", "redu", "refe", "refo", "refu", "regi", "regu", "rele",
		"reli", "reme", "remo", "rent", "repo", "repr", "resi", "reso", "ress", "rest",
		"rgen", "rget", "ribe", "rick", "rict", "ring", "rive", "rmor", "road", "rock",
		"role", "romo", "rong", "ront", "room", "rope", "roun", "rout", "rove", "rule",
		"safe", "safe", "sage", "sage", "sand", "sati", "scan", "scen", "scho", "scor",
		"scou", "scri", "sear", "seas", "seat", "sect", "sect", "secu", "sens", "sent",
		"sequ", "seri", "serv", "seve", "shad", "shar", "shel", "ship", "shop", "shot",
		"show", "side", "sign", "sist", "soci", "soft", "spac", "spar", "spec", "spee",
		"spir", "spir", "spit", "spla", "spri", "squa", "squa", "stab", "staf", "stag",
		"stag", "stak", "star", "stat", "step", "ster", "stit", "stoc", "stop", "stor",
		"stra", "stre", "stri", "stro", "stro", "stru", "stud", "subs", "subs", "succ",
		"sult", "summ", "supp", "sure", "tabl", "tack", "tact", "tain", "take", "tall",
		"tate", "team", "tech", "temp", "tend", "tent", "terf", "term", "terr", "test",
		"ther", "thor", "time", "tion", "tomo", "tone", "tore", "tory", "tour", "town",
		"trac", "trad", "trav", "trav", "trea", "tree", "trib", "tric", "trip", "trol",
		"tron", "tter", "ttle", "ture", "turn", "type", "type", "udge", "uenc", "ulat",
		"ulti", "unch", "unde", "unit", "urch", "urse", "ussi", "valu", "veal", "vehi",
		"velo", "vent", "verh", "vers", "vert", "vest", "vice", "vict", "vide", "view",
		"vill", "visi", "volu", "vote", "walk", "wall", "wall", "want", "ward", "ware",
		"wash", "week", "whol", "whol", "wind", "wing", "work", "writ", "xper", "year"
	};

	private static final String[] l5g = {
		"advan", "agree", "allow", "ameri", "anger", "assoc", "ation", "attle", "austr", "avail",
		"award", "battl", "black", "board", "break", "broad", "broth", "build", "busin", "ceive",
		"centr", "centu", "chanc", "chang", "chara", "charg", "child", "claim", "class", "clude",
		"commo", "compa", "compe", "confe", "confi", "conne", "const", "consu", "conta", "conte",
		"contr", "conve", "counc", "count", "cours", "court", "cover", "creat", "cross", "curre",
		"decid", "defen", "democ", "depar", "depen", "desig", "devel", "direc", "disco", "distr",
		"drive", "ealth", "earin", "earth", "eason", "easur", "educa", "egion", "egree", "elect",
		"ember", "emplo", "enter", "eport", "equen", "ermin", "escri", "estab", "event", "expec",
		"exten", "famil", "fathe", "featu", "fight", "finan", "front", "futur", "gener", "gover",
		"grand", "graph", "green", "gress", "groun", "happe", "heser", "house", "human", "icipa",
		"iddle", "ident", "impor", "indus", "infor", "insta", "instr", "inter", "inves", "islan",
		"level", "light", "limit", "llion", "locat", "manag", "march", "marke", "matio", "media",
		"membe", "milit", "milli", "minut", "mmuni", "model", "money", "mount", "natio", "natur",
		"ngine", "night", "offic", "ology", "ommun", "ontra", "order", "organ", "ouble", "ounce",
		"paper", "paren", "peopl", "phone", "pital", "place", "plain", "plete", "polic", "polit",
		"posit", "ppear", "pport", "pract", "press", "price", "princ", "proba", "proce", "produ",
		"progr", "proje", "prope", "prote", "prove", "publi", "quart", "quest", "quick", "raise",
		"range", "recom", "recor", "reduc", "refer", "refor", "regul", "repla", "repor", "repub",
		"resid", "ridge", "right", "river", "roduc", "round", "schoo", "scien", "score", "searc",
		"seaso", "secur", "simil", "sland", "slate", "socia", "sound", "space", "speak", "spect",
		"spend", "spite", "splay", "sport", "stabl", "stake", "stand", "start", "state", "stati",
		"stock", "stone", "store", "story", "strai", "strat", "stree", "struc", "style", "super",
		"table", "teach", "techn", "tempt", "theat", "theor", "therm", "think", "track", "tract",
		"trade", "train", "trans", "trave", "treat", "truct", "ultur", "ument", "under", "unive",
		"velop", "villA", "ville", "volve", "watch", "water", "woman", "world", "write", "ystem"
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
