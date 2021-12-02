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
// AW file GramMap.java : 30nov2021 CPM
// lookup for 3-, 4-, and 5-grams

package gram;

import java.util.*;

public class GramMap {

	private static final String[] l4g = {
		"aban", "abit", "able", "acce", "acci", "acco", "ache", "acid", "acon", "acre",
		"adem", "adet", "adge", "admi", "adve", "aero", "agen", "ague", "aign", "aile",
		"aint", "aire", "aise", "alan", "alco", "alle", "allo", "ally", "alte", "alve",
		"ambl", "amer", "amin", "amou", "ampl", "anal", "anar", "anat", "ance", "anch",
		"andi", "andl", "ange", "anon", "anso", "anta", "anti", "anyo", "apar", "appe",
		"appl", "appo", "appr", "apse", "aqua", "arab", "aran", "arbo", "arch", "area",
		"arge", "arma", "arni", "arra", "arre", "arri", "arro", "arry", "arse", "arth",
		"arti", "asia", "asis", "aske", "ason", "assa", "assi", "assu", "aste", "astr",
		"asur", "atar", "atch", "ater", "athe", "atin", "atio", "atri", "atta", "atte",
		"atto", "audi", "ault", "aunt", "ause", "aust", "auth", "auto", "avai", "aven",
		"avid", "awar", "back", "bact", "bait", "bake", "bala", "bald", "ball", "band",
		"bang", "bank", "bann", "barr", "base", "bate", "batt", "bble", "bear", "beat",
		"beer", "bell", "belt", "bene", "bent", "berg", "best", "bibl", "bill", "bind",
		"bine", "biol", "bion", "bird", "bite", "bjec", "blea", "blem", "bloo", "blow",
		"blue", "boat", "body", "bolt", "bomb", "bond", "bone", "book", "boom", "born",
		"boro", "bott", "boul", "brai", "bran", "brav", "brea", "brew", "brid", "brit",
		"broa", "brow", "buck", "buff", "buil", "bulk", "bull", "bump", "burg", "burn",
		"bury", "bush", "bust", "bute", "butt", "byte", "cade", "cage", "cake", "cala",
		"calc", "cale", "cali", "call", "calm", "camp", "cana", "canc", "cand", "cann",
		"capa", "cape", "capt", "card", "care", "carp", "carr", "cart", "case", "cash",
		"cast", "cata", "cate", "caus", "ceiv", "cele", "cell", "cend", "cent", "cept",
		"cere", "cern", "cert", "cess", "chal", "cham", "chan", "char", "chas", "chea",
		"chem", "chen", "cheo", "chet", "chic", "chie", "chin", "chip", "chis", "choo",
		"chop", "chor", "chri", "chro", "chur", "cide", "cien", "cile", "cind", "cise",
		"cite", "city", "civi", "cket", "ckle", "clap", "clar", "cler", "clim", "clin",
		"clip", "clos", "club", "coat", "cock", "cold", "coll", "colo", "colt", "colu",
		"comb", "come", "comm", "comp", "conc", "cond", "cone", "conf", "cong", "conn",
		"cons", "cont", "conv", "cook", "cool", "cope", "cord", "core", "corn", "corp",
		"corr", "cosm", "cost", "cote", "cott", "coun", "coup", "cour", "cove", "crab",
		"cram", "cran", "crap", "crat", "crea", "cred", "crep", "cret", "crew", "crip",
		"crit", "crop", "crow", "cruc", "crum", "cryp", "ctic", "cula", "cule", "cult",
		"curi", "curr", "curt", "cuse", "cuss", "cust", "cute", "cyte", "cyto", "dale",
		"damp", "dana", "danc", "dang", "dard", "dare", "dark", "dash", "data", "date",
		"daug", "dawn", "ddle", "dead", "deal", "dean", "dear", "deat", "deca", "dece",
		"deci", "decl", "deep", "deer", "defe", "defi", "dela", "dele", "deli", "delt",
		"delu", "deme", "demi", "demo", "dens", "dent", "depe", "depo", "dera", "desc",
		"desi", "desp", "dest", "dete", "deve", "devi", "dict", "dida", "dier", "diet",
		"diff", "digi", "dine", "dirt", "disa", "disc", "dise", "dish", "disi", "disp",
		"dist", "dium", "divi", "dome", "dona", "done", "doom", "door", "dors", "dose",
		"dote", "down", "doze", "drag", "draw", "drea", "dres", "driv", "drop", "drug",
		"drum", "duce", "duck", "dump", "dunk", "dupl", "dusk", "dust", "each", "eant",
		"eard", "earm", "earn", "eart", "ease", "easo", "east", "eath", "eave", "ebat",
		"ebra", "ebri", "ecal", "echo", "ecia", "ecip", "ecla", "ecom", "econ", "ecor",
		"ecre", "ectr", "edal", "eden", "eder", "edes", "edge", "edit", "educ", "eech",
		"eeti", "eeze", "efea", "egen", "egot", "eigh", "eign", "eleb", "elec", "eleg",
		"elem", "eles", "elig", "elim", "elin", "elit", "elon", "elte", "elve", "emai",
		"eman", "embe", "embl", "embr", "emed", "emem", "emen", "emer", "emis", "emon",
		"emor", "empl", "empt", "enal", "enam", "ence", "ench", "enef", "enfo", "enom",
		"enor", "ense", "ensu", "ente", "enti", "entr", "eopl", "epar", "epor", "epre",
		"eput", "equa", "eque", "equi", "erag", "eral", "erap", "erce", "erch", "erit",
		"erro", "erry", "erse", "erso", "erty", "erve", "esca", "esig", "esis", "esth",
		"esti", "estr", "etai", "etal", "etch", "eter", "ethe", "etro", "ette", "ettl",
		"etto", "eval", "evel", "even", "ever", "evic", "evid", "evil", "exam", "exce",
		"expe", "expl", "exte", "extr", "face", "fact", "fail", "fair", "fall", "fals",
		"fant", "fare", "farm", "fasc", "fast", "fate", "favo", "feat", "fect", "fede",
		"feed", "feel", "feit", "femi", "fess", "fest", "ffer", "ffle", "ffor", "fice",
		"fict", "fiel", "file", "fill", "film", "fina", "find", "fini", "fire", "firm",
		"fish", "fiss", "flag", "flex", "flip", "floo", "flop", "flor", "flow", "fold",
		"foli", "folk", "foll", "fond", "food", "foot", "forc", "ford", "fore", "forg",
		"form", "fort", "frag", "frat", "free", "fres", "fric", "fter", "fuel", "fuge",
		"full", "fume", "func", "fund", "fuse", "gain", "gall", "game", "gant", "garb",
		"gard", "gate", "gear", "genc", "gend", "gene", "gent", "geri", "germ", "gest",
		"giga", "gine", "girl", "give", "glad", "glan", "glen", "glob", "goat", "gold",
		"golf", "gone", "good", "goon", "gote", "grad", "gram", "grap", "grat", "grav",
		"gray", "gree", "gren", "grou", "grow", "grum", "guar", "guil", "gulf", "gull",
		"gust", "hair", "half", "hall", "hame", "hamp", "hanc", "hand", "hank", "hant",
		"happ", "hard", "harm", "harp", "hart", "hase", "hast", "hate", "have", "hawk",
		"head", "heal", "hear", "heat", "heav", "heel", "heir", "hell", "helm", "help",
		"hema", "hemi", "hemo", "hend", "herd", "heri", "hero", "hest", "hexa", "hick",
		"hief", "high", "hill", "hind", "hine", "hing", "hint", "hist", "hman", "hold",
		"hole", "home", "hone", "hool", "hope", "hore", "hori", "horn", "horr", "host",
		"hour", "hous", "hull", "hump", "hunt", "hydr", "hyst", "ibut", "ican", "icat",
		"icer", "icip", "idea", "idge", "ield", "ient", "ieve", "igat", "igen", "iger",
		"ight", "igni", "ilet", "imen", "imet", "imit", "imme", "immu", "impa", "impl",
		"impo", "impr", "inap", "inch", "inci", "inco", "inde", "indu", "inet", "infl",
		"info", "inge", "ingo", "ingu", "inno", "inse", "insp", "inst", "insu", "inta",
		"inte", "inti", "intr", "inve", "invo", "ipse", "ique", "iron", "isit", "isla",
		"isle", "ispo", "issu", "iste", "istr", "itar", "itch", "iter", "ithe", "itio",
		"itis", "ittl", "itut", "ivat", "iver", "ivil", "izen", "jack", "jail", "japa",
		"jazz", "jean", "ject", "jers", "jest", "join", "jour", "jump", "junk", "jure",
		"jury", "just", "keep", "kick", "kill", "kilo", "kind", "kine", "king", "kiss",
		"kitc", "know", "labo", "lace", "lack", "lact", "lade", "lady", "lage", "laim",
		"land", "lane", "lang", "lant", "lapi", "lare", "larg", "lari", "lash", "lass",
		"last", "late", "lati", "lawn", "lead", "lean", "lear", "leas", "lebr", "lect",
		"leep", "lega", "lend", "lent", "less", "lete", "lett", "lice", "lick", "lict",
		"life", "lift", "lige", "ligi", "limb", "lime", "limi", "lind", "line", "ling",
		"lint", "lion", "liqu", "list", "lite", "lith", "live", "llab", "llet", "lley",
		"llow", "load", "loan", "loca", "lock", "logi", "lone", "long", "look", "loom",
		"loon", "lope", "loqu", "lord", "lore", "loss", "love", "lter", "luck", "lude",
		"lumb", "lume", "lump", "lung", "lure", "lust", "lute", "mach", "mage", "maid",
		"mail", "main", "majo", "make", "mala", "male", "mall", "manc", "mand", "mang",
		"mani", "mank", "mans", "mant", "manu", "mare", "mari", "mark", "marr", "mart",
		"mary", "masc", "mass", "mast", "mate", "math", "matr", "matt", "mber", "mbin",
		"mble", "meal", "mean", "meas", "meat", "mech", "meda", "medi", "medy", "meet",
		"mega", "melt", "memo", "mend", "mens", "ment", "mera", "merc", "merg", "mess",
		"mest", "mete", "meth", "metr", "migr", "mile", "mili", "milk", "mill", "mind",
		"mine", "mini", "mint", "minu", "mira", "misc", "miss", "mist", "mmer", "mmis",
		"mmit", "mode", "mold", "mond", "mone", "mont", "moon", "moor", "more", "morn",
		"mort", "mote", "moti", "moun", "move", "movi", "mpac", "mpar", "mpas", "mpat",
		"mper", "mple", "muni", "musc", "musi", "myst", "nage", "nake", "name", "nate",
		"nati", "natu", "naut", "ncia", "ndam", "nder", "ndic", "ndle", "neck", "nect",
		"need", "nega", "nerg", "nest", "netw", "neut", "ngag", "nger", "ngin", "ngle",
		"ngra", "ngth", "nice", "nick", "nion", "nior", "nkle", "nman", "nnel", "noon",
		"norm", "nose", "note", "noti", "noun", "nser", "nsio", "nsis", "nsit", "nsti",
		"nstr", "nten", "nter", "ntin", "ntry", "nute", "oach", "oard", "oast", "obab",
		"ocen", "ocke", "octo", "ocus", "odel", "odge", "offe", "offi", "ogra", "oice",
		"oist", "olat", "ollo", "olve", "oman", "ombi", "omen", "omet", "omma", "ompa",
		"ompe", "ompl", "onar", "onat", "onde", "ondo", "ondu", "onen", "oney", "ongr",
		"onic", "onom", "onor", "onsc", "onst", "ontr", "oodl", "ooth", "open", "oper",
		"opin", "ople", "opti", "orge", "orig", "orit", "orni", "orri", "orth", "ortu",
		"osis", "osit", "ossi", "otch", "otel", "othe", "ottl", "oubl", "ouch", "ough",
		"ounc", "ound", "oung", "ount", "ourn", "ouse", "oust", "oute", "outh", "oven",
		"over", "ower", "oyal", "pace", "pack", "pact", "page", "pain", "pair", "pale",
		"pali", "pall", "palm", "pand", "pane", "pani", "pant", "para", "pare", "pari",
		"park", "parl", "part", "pass", "past", "pate", "path", "patr", "patt", "peak",
		"pear", "pect", "pede", "pell", "pena", "pend", "peni", "pent", "peop", "perc",
		"perf", "peri", "perm", "pers", "pert", "pest", "pete", "peti", "phil", "phra",
		"phys", "pick", "pict", "pike", "pill", "pine", "pipe", "pise", "pist", "plac",
		"plan", "plas", "plat", "play", "plet", "plex", "plic", "ploy", "pock", "poin",
		"pois", "poke", "pole", "poli", "poll", "pond", "pone", "pool", "popu", "pore",
		"port", "pose", "posi", "post", "ppea", "ppen", "pple", "ppli", "prea", "prec",
		"pred", "prem", "prep", "pres", "pret", "prev", "pric", "prim", "prin", "prio",
		"pris", "priv", "prob", "proc", "prof", "prom", "prop", "pros", "prot", "prov",
		"pter", "publ", "pull", "pump", "push", "pute", "quad", "quan", "quar", "quen",
		"quer", "ques", "quir", "quit", "race", "rack", "ract", "rade", "radi", "raft",
		"rage", "raid", "rail", "rain", "rald", "rama", "ramb", "rame", "ramp", "ranc",
		"rand", "rang", "rank", "rans", "rape", "rash", "rass", "rast", "rate", "rath",
		"rati", "rato", "rcen", "reac", "read", "reak", "real", "ream", "reap", "rear",
		"reas", "reat", "rebe", "rebo", "reca", "rece", "reck", "reco", "rect", "rede",
		"redi", "redu", "reed", "reet", "refe", "refo", "refu", "regi", "regu", "rela",
		"rele", "reli", "reme", "remo", "rend", "rent", "repe", "repo", "repr", "repu",
		"resc", "resh", "resi", "reso", "resp", "ress", "rest", "resu", "reta", "retr",
		"reve", "rgen", "rget", "ribe", "rice", "rich", "rick", "rict", "ride", "rift",
		"rike", "rill", "rime", "rina", "rine", "ring", "rink", "ripe", "ript", "rise",
		"rish", "risk", "rita", "rive", "rize", "rman", "rmor", "rnet", "road", "robe",
		"robo", "rock", "rode", "roga", "roid", "roke", "role", "roll", "roma", "romo",
		"rone", "rong", "roni", "ront", "rook", "room", "roon", "root", "rope", "roph",
		"rose", "roun", "roup", "rous", "rout", "rove", "rran", "rres", "rrow", "rtun",
		"rude", "rule", "rump", "rupt", "rush", "rust", "sack", "sacr", "sade", "safe",
		"sage", "sail", "sala", "sale", "sali", "salo", "sand", "sane", "sani", "sano",
		"sati", "scal", "scan", "scar", "scen", "sche", "scho", "scor", "scot", "scou",
		"scra", "scri", "scru", "scur", "scus", "seal", "sear", "seas", "seat", "sect",
		"secu", "seed", "sele", "self", "sell", "semi", "send", "sens", "sent", "sequ",
		"seri", "sers", "sert", "serv", "sess", "seve", "shad", "sham", "shan", "shap",
		"shar", "shea", "shel", "shie", "ship", "shir", "shoo", "shop", "shor", "shot",
		"show", "sick", "side", "sign", "sile", "sing", "sink", "sire", "sist", "site",
		"size", "sket", "skin", "slam", "slap", "slea", "slid", "slin", "slip", "slow",
		"sman", "snap", "snip", "snow", "soci", "sock", "soft", "sole", "solu", "some",
		"song", "soph", "sore", "sort", "sour", "spac", "span", "spar", "spec", "spee",
		"spel", "spir", "spit", "spla", "spon", "spot", "spri", "spur", "squa", "squi",
		"ssen", "sset", "stab", "staf", "stag", "stai", "stak", "stal", "stam", "stan",
		"star", "stat", "stay", "stel", "stem", "sten", "step", "ster", "stic", "stif",
		"stig", "stit", "stle", "stoc", "stom", "ston", "stop", "stor", "stra", "stre",
		"stri", "stro", "stru", "stud", "subs", "succ", "suit", "sult", "sume", "summ",
		"sump", "supp", "supr", "sure", "surg", "surv", "susp", "symb", "synt", "tabl",
		"tach", "tack", "tact", "tail", "tain", "take", "tale", "tali", "talk", "tall",
		"tamp", "tang", "tank", "tard", "targ", "tate", "team", "tech", "tect", "teen",
		"tele", "tell", "temp", "tena", "tenc", "tend", "teno", "tens", "tent", "terc",
		"terf", "term", "tern", "terr", "terv", "test", "text", "thar", "them", "theo",
		"ther", "thin", "thol", "thon", "thor", "thre", "thro", "tick", "ticl", "tige",
		"tile", "tilt", "timb", "time", "tina", "tine", "ting", "tinu", "tion", "tire",
		"tiss", "tman", "tole", "toll", "tomb", "tomo", "tone", "tono", "tool", "toon",
		"toot", "tore", "tort", "tory", "tour", "tout", "town", "trac", "trad", "trag",
		"trai", "tran", "trap", "trav", "trea", "tree", "trem", "tren", "tres", "tria",
		"trib", "tric", "trig", "trip", "trol", "tron", "trop", "trou", "troy", "truc",
		"true", "trum", "trun", "trus", "tter", "ttle", "tude", "tume", "turb", "ture",
		"turn", "tute", "twin", "type", "uard", "uate", "ubli", "udge", "uenc", "uest",
		"ught", "uite", "ulat", "ulge", "ulti", "umbl", "umor", "umph", "unce", "unch",
		"uncl", "unct", "unde", "unge", "ungu", "unic", "unio", "unit", "uple", "urch",
		"uret", "urge", "urse", "ussi", "ustr", "utch", "utin", "vade", "valu", "vant",
		"veal", "vect", "vehi", "velo", "vene", "vent", "verb", "verh", "veri", "vers",
		"vert", "vest", "vice", "vict", "vide", "view", "vill", "vinc", "vine", "viol",
		"visi", "vite", "vive", "void", "voke", "volt", "volu", "vote", "voup", "wait",
		"wake", "walk", "wall", "want", "ward", "ware", "warm", "warn", "warr", "wash",
		"wast", "weal", "wear", "week", "weep", "well", "west", "whis", "whol", "wick",
		"wide", "wife", "wild", "wind", "wine", "wing", "wipe", "wire", "wish", "wist",
		"word", "work", "worl", "worm", "wrap", "writ", "xper", "xplo", "yard", "yarn",
		"year", "yoff", "yond", "youn", "yout", "zero", "zest", "zone", "zoom", "zzle"
	};

	private static final String[] l5g = {
		"actic", "actor", "advan", "agent", "agree", "alleg", "allow", "ament", "ameri", "anger",
		"anima", "apply", "arden", "assoc", "atern", "ation", "attle", "audio", "austr", "avail",
		"award", "battl", "beach", "black", "blend", "block", "blood", "board", "bread", "break",
		"brick", "broad", "brook", "broth", "brown", "build", "busin", "cargo", "ceive", "centr",
		"centu", "chair", "chanc", "chang", "chant", "chara", "charg", "chase", "cheap", "check",
		"cheer", "chees", "cheme", "chest", "chief", "child", "circl", "circu", "claim", "class",
		"clean", "clear", "cliff", "cline", "clock", "close", "cloth", "clown", "clude", "coast",
		"colon", "commo", "compa", "compe", "confe", "confi", "conne", "const", "consu", "conta",
		"conte", "contr", "conve", "counc", "count", "cours", "court", "cover", "craft", "crash",
		"creat", "creek", "crime", "cross", "crown", "crude", "curre", "custo", "cycle", "dairy",
		"decid", "defen", "democ", "depar", "depen", "desig", "devel", "direc", "disco", "distr",
		"draft", "dress", "drift", "drink", "drive", "dynam", "ealth", "earin", "earth", "eason",
		"easur", "educa", "egion", "egree", "eight", "elect", "ellow", "emand", "ember", "emplo",
		"energ", "enjoy", "enter", "eport", "equen", "equip", "ermin", "escri", "estab", "estig",
		"estim", "event", "exert", "expec", "exten", "famil", "fashi", "fathe", "featu", "fever",
		"field", "fight", "figur", "final", "finan", "flavo", "flict", "force", "fores", "forge",
		"found", "frame", "fraud", "frien", "front", "fruit", "futur", "gener", "glass", "globe",
		"gover", "grace", "grade", "grain", "grand", "grant", "graph", "green", "greet", "gress",
		"grind", "groom", "groun", "grove", "hance", "happe", "happy", "haven", "heart", "heath",
		"highw", "histo", "hollo", "horse", "hotel", "house", "human", "icate", "icide", "icipa",
		"iddle", "ident", "imate", "impor", "index", "indus", "iness", "infor", "insta", "instr",
		"integ", "inter", "intro", "inves", "islan", "issue", "isten", "janit", "judge", "jungl",
		"large", "later", "laugh", "learn", "level", "light", "limit", "litic", "llage", "llege",
		"lleng", "llion", "locat", "lunch", "macro", "manag", "manor", "march", "marke", "marsh",
		"maste", "matio", "meado", "media", "medic", "membe", "merge", "micro", "might", "milit",
		"milli", "minat", "minut", "mmuni", "model", "money", "month", "motor", "mount", "movie",
		"music", "nance", "natio", "natur", "neigh", "neral", "neutr", "ngine", "night", "nsist",
		"ntern", "nurse", "ocean", "offic", "ology", "ommun", "ontra", "opera", "orbit", "order",
		"organ", "otent", "other", "ouble", "ounce", "paign", "paper", "paren", "party", "peace",
		"peopl", "perio", "phase", "phone", "photo", "pital", "place", "plain", "plane", "plant",
		"plent", "plete", "plica", "plore", "point", "polic", "polit", "posit", "pound", "power",
		"ppear", "pport", "pract", "press", "price", "princ", "pring", "prise", "proba", "proce",
		"produ", "progr", "proje", "prope", "prosp", "prote", "proto", "prove", "publi", "pulse",
		"quart", "quest", "quick", "radio", "raise", "ranch", "range", "reach", "recom", "recor",
		"reduc", "refer", "refor", "regul", "rench", "repla", "repor", "repub", "resid", "ridge",
		"right", "river", "roduc", "round", "scale", "scape", "schoo", "scien", "score", "searc",
		"seaso", "secur", "senat", "sense", "shack", "share", "shark", "sheep", "sheet", "shell",
		"shine", "shire", "shirt", "shore", "short", "sight", "simil", "skate", "sland", "slate",
		"smart", "smash", "smoke", "socia", "solve", "sound", "sourc", "space", "speak", "spear",
		"spect", "spend", "spill", "spire", "spite", "splay", "sport", "sprin", "squad", "squar",
		"ssist", "stabl", "staff", "stain", "stake", "stall", "stand", "start", "state", "stati",
		"stead", "steal", "steam", "steel", "steer", "still", "stock", "stone", "store", "storm",
		"story", "strai", "strat", "stree", "strik", "strip", "struc", "stuff", "style", "subst",
		"super", "sweet", "table", "teach", "techn", "tempt", "teria", "terra", "thank", "theat",
		"theor", "therm", "think", "tight", "toast", "tower", "toxic", "track", "tract", "trade",
		"trail", "train", "trans", "trave", "treat", "treme", "trial", "trict", "trong", "truck",
		"truct", "trust", "tutor", "ublic", "ultur", "ument", "under", "unive", "ustle", "valle",
		"value", "velop", "venue", "veter", "video", "villa", "ville", "vista", "volve", "watch",
		"water", "white", "winte", "woman", "world", "worth", "wreck", "write", "yield", "ystem"
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
