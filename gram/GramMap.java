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
// AW file GramMap.java : 28dec2021 CPM
// lookup fori built-in 3-, 4-, and 5-grams

package gram;

import java.util.*;

public class GramMap {

	private static final String[] l4g = {
		"aban", "abbl", "abit", "able", "abor", "acce", "acci", "acco", "ache", "acid",
		"acle", "acon", "acre", "adem", "adet", "adge", "adle", "admi", "adve", "aero",
		"agen", "aggl", "aggr", "agle", "agra", "agri", "ague", "aign", "aile", "aint",
		"aire", "aise", "alan", "alco", "alem", "alia", "alle", "allo", "ally", "alon",
		"aloo", "alte", "alve", "ambi", "ambl", "amer", "amin", "amou", "ampl", "anal",
		"anar", "anat", "ance", "anch", "andi", "andl", "aneu", "ange", "angl", "anon",
		"anso", "anta", "anti", "anyo", "apar", "appe", "appl", "appo", "appr", "apse",
		"aqua", "arab", "aran", "arbi", "arbo", "arch", "area", "aren", "arge", "arma",
		"arni", "aron", "arra", "arre", "arri", "arro", "arry", "arse", "arte", "arth",
		"arti", "asci", "asia", "asis", "aske", "ason", "assa", "asse", "assi", "assu",
		"aste", "asto", "astr", "asur", "atar", "atch", "ater", "athe", "atin", "atio",
		"atri", "atta", "atte", "atti", "atto", "attr", "audi", "ault", "aunt", "ause",
		"aust", "auth", "auto", "avai", "aven", "avid", "awar", "axis", "babe", "baby",
		"back", "bact", "bait", "bake", "bala", "bald", "ball", "balm", "band", "bang",
		"bank", "bann", "barb", "bare", "bark", "barr", "base", "bate", "bath", "batt",
		"bble", "bead", "bean", "bear", "beat", "beef", "beer", "bell", "belt", "bend",
		"bene", "bent", "berg", "best", "beve", "bibl", "bill", "bind", "bine", "bing",
		"biol", "bion", "bird", "bite", "bjec", "blas", "blea", "blem", "bloo", "blow",
		"blue", "boar", "boat", "body", "bolt", "bomb", "bond", "bone", "book", "boom",
		"boon", "born", "boro", "bott", "boul", "brag", "brai", "bran", "brav", "brea",
		"bred", "brew", "brid", "brit", "broa", "brow", "buck", "buff", "buil", "bulk",
		"bull", "bump", "bund", "bung", "bunk", "burg", "burn", "bury", "bush", "bust",
		"bute", "butt", "byte", "cade", "cage", "cake", "cala", "calc", "cale", "cali",
		"call", "calm", "camp", "cana", "canc", "cand", "cann", "cant", "capa", "cape",
		"capi", "capt", "card", "care", "carn", "carp", "carr", "cart", "case", "cash",
		"cast", "cata", "cate", "caus", "cava", "cave", "ceiv", "cele", "cell", "cend",
		"cens", "cent", "cept", "cere", "cern", "cert", "cess", "chal", "cham", "chan",
		"chap", "char", "chas", "chea", "chem", "chen", "cheo", "chet", "chic", "chie",
		"chin", "chip", "chis", "choo", "chop", "chor", "chow", "chri", "chro", "chur",
		"cial", "cide", "cien", "cile", "cind", "cise", "cite", "city", "civi", "cket",
		"ckle", "clap", "clar", "claw", "cler", "clim", "clin", "clip", "clos", "club",
		"coar", "coat", "cock", "coco", "cold", "coll", "colo", "colt", "colu", "comb",
		"come", "comm", "comp", "conc", "cond", "cone", "conf", "cong", "conn", "cons",
		"cont", "conv", "cook", "cool", "coop", "cope", "cord", "core", "cork", "corn",
		"corp", "corr", "cosm", "cost", "cote", "cott", "coun", "coup", "cour", "cove",
		"crab", "cram", "cran", "crap", "crat", "craz", "crea", "cred", "cree", "crep",
		"cret", "crew", "crib", "crip", "crit", "crop", "crow", "cruc", "crui", "crum",
		"cryp", "ctic", "cuff", "cula", "cule", "cult", "cumb", "curi", "curr", "curt",
		"cuse", "cuss", "cust", "cute", "cyte", "cyto", "dale", "damp", "dana", "danc",
		"dang", "dard", "dare", "dark", "dash", "data", "date", "daug", "dawn", "ddle",
		"dead", "deal", "dean", "dear", "deat", "deca", "dece", "deci", "decl", "deep",
		"deer", "defe", "defi", "dela", "dele", "deli", "delt", "delu", "deme", "demi",
		"demo", "dens", "dent", "deny", "depe", "depo", "dera", "desc", "desi", "desp",
		"dest", "dete", "deve", "devi", "dict", "dida", "dier", "diet", "diff", "digi",
		"dine", "ding", "dirt", "disa", "disc", "dise", "dish", "disi", "disp", "dist",
		"dium", "divi", "dole", "dome", "dona", "done", "dong", "dook", "doom", "door",
		"dorm", "dors", "dose", "dote", "down", "doze", "drag", "draw", "drea", "dres",
		"drip", "driv", "drop", "drow", "drug", "drum", "duce", "duck", "dump", "dunk",
		"dupl", "dusk", "dust", "dyna", "each", "eagl", "eant", "eard", "earm", "earn",
		"eart", "ease", "easo", "east", "eate", "eath", "eave", "ebat", "ebra", "ebri",
		"ebut", "ecal", "echo", "ecia", "ecip", "ecla", "ecom", "econ", "ecor", "ecre",
		"ecto", "ectr", "edal", "eden", "eder", "edes", "edge", "edit", "educ", "eech",
		"eeti", "eeze", "efea", "egen", "egim", "egot", "eigh", "eign", "elan", "eleb",
		"elec", "eleg", "elem", "eles", "elig", "elim", "elin", "elit", "elon", "elte",
		"elve", "emai", "eman", "embe", "embl", "embr", "emed", "emem", "emen", "emer",
		"emis", "emon", "emor", "emot", "empl", "empt", "enal", "enam", "ence", "ench",
		"enef", "enfo", "enom", "enor", "ense", "ensu", "ente", "enti", "entr", "eopl",
		"epar", "epor", "epre", "eput", "equa", "eque", "equi", "erag", "eral", "erap",
		"erce", "erch", "eril", "erio", "eris", "erit", "erro", "erry", "erse", "erso",
		"erty", "erua", "erve", "esca", "esig", "esis", "esth", "esti", "estl", "estr",
		"etai", "etal", "etch", "eter", "ethe", "ethi", "etho", "etro", "ette", "ettl",
		"etto", "etty", "eval", "evel", "even", "ever", "evic", "evid", "evil", "exam",
		"exce", "expe", "expl", "exte", "extr", "face", "fact", "fail", "fair", "fall",
		"fals", "fame", "fang", "fant", "fare", "farm", "fasc", "fast", "fate", "favo",
		"fear", "feat", "fect", "fede", "feed", "feel", "feit", "feli", "fell", "femi",
		"fend", "fern", "ferr", "fess", "fest", "ffer", "ffle", "ffor", "fice", "fict",
		"fiel", "figu", "file", "fill", "film", "fina", "find", "fine", "fini", "fire",
		"firm", "fish", "fiss", "fist", "flag", "flai", "flam", "flan", "flee", "flex",
		"flim", "flip", "floo", "flop", "flor", "flou", "flow", "foil", "fold", "foli",
		"folk", "foll", "fond", "food", "fool", "foot", "forc", "ford", "fore", "forg",
		"fork", "form", "fort", "foul", "frag", "frai", "frat", "free", "fres", "fric",
		"frug", "fter", "fuel", "fuge", "full", "fume", "func", "fund", "fung", "furn",
		"fuse", "fuss", "gain", "gall", "game", "gang", "gant", "garb", "gard", "gate",
		"gave", "gear", "genc", "gend", "gene", "geni", "gent", "geon", "geri", "germ",
		"gest", "ggle", "giga", "gile", "gine", "girl", "give", "glad", "glan", "glen",
		"glob", "gnal", "goat", "gold", "golf", "gone", "good", "goon", "gorg", "gory",
		"gote", "grad", "gram", "grap", "grat", "grav", "gray", "gree", "gren", "grin",
		"grou", "grow", "grum", "guar", "guil", "gulf", "gull", "gust", "hack", "hair",
		"half", "hall", "hame", "hamp", "hanc", "hand", "hank", "hant", "happ", "hard",
		"harm", "harp", "hart", "hase", "hast", "hate", "have", "hawk", "hbor", "head",
		"heal", "heap", "hear", "heat", "heav", "heel", "heir", "hell", "helm", "help",
		"hema", "hemi", "hemo", "hend", "hera", "herb", "herd", "here", "heri", "hero",
		"hest", "hexa", "hibi", "hick", "hief", "high", "hill", "hind", "hine", "hing",
		"hint", "hist", "hive", "hman", "hock", "hoke", "hold", "hole", "holy", "home",
		"hone", "hook", "hool", "hope", "hore", "hori", "horn", "horr", "hose", "host",
		"hour", "hous", "hull", "humb", "hump", "hund", "hunt", "hurl", "hydr", "hyst",
		"iber", "ibut", "ican", "icat", "icer", "icip", "icle", "idea", "idge", "idol",
		"ield", "ient", "ieve", "igat", "igen", "iger", "ight", "igni", "ilet", "ilia",
		"illa", "ille", "imag", "imen", "imet", "imit", "imme", "immo", "immu", "impa",
		"impe", "impl", "impo", "impr", "imul", "inap", "ince", "inch", "inci", "inco",
		"inde", "indi", "indu", "inet", "infl", "info", "inge", "ingo", "ingu", "inno",
		"inse", "insp", "inst", "insu", "inta", "inte", "inti", "intr", "inve", "invo",
		"iple", "ipro", "ipse", "ique", "iron", "irra", "irre", "irri", "isci", "isit",
		"isla", "isle", "ison", "ispo", "issu", "iste", "istl", "istr", "itar", "itch",
		"iter", "ithe", "ithm", "itio", "itis", "ittl", "itud", "itut", "ival", "ivat",
		"ivel", "iver", "ivil", "izen", "jack", "jail", "japa", "jazz", "jean", "ject",
		"jerk", "jers", "jest", "jing", "join", "jour", "jump", "juni", "junk", "jure",
		"jury", "just", "keep", "kick", "kill", "kilo", "kind", "kine", "king", "kiss",
		"kitc", "knee", "know", "labo", "lace", "lack", "lact", "lade", "lady", "lage",
		"laim", "lain", "lamb", "lamp", "land", "lane", "lang", "lank", "lant", "lapi",
		"lare", "larg", "lari", "lash", "lass", "last", "late", "lati", "lava", "lawn",
		"lead", "lean", "lear", "leas", "leav", "lebr", "lect", "leep", "left", "lega",
		"lend", "lent", "leon", "less", "lest", "lete", "lett", "libr", "lice", "lick",
		"lict", "lien", "life", "liff", "lift", "lige", "ligi", "like", "limb", "lime",
		"limi", "limp", "lind", "line", "ling", "link", "lint", "lion", "liqu", "list",
		"lite", "lith", "live", "llab", "llar", "llet", "lley", "llow", "load", "loan",
		"loca", "lock", "loft", "logi", "lone", "long", "look", "loom", "loon", "lope",
		"loqu", "lord", "lore", "loss", "loud", "love", "lter", "luck", "lude", "lumb",
		"lume", "lump", "luna", "lung", "lunk", "lure", "lush", "lust", "lute", "mach",
		"mage", "maid", "mail", "main", "majo", "make", "mala", "male", "mall", "manc",
		"mand", "mang", "mani", "mank", "mans", "mant", "manu", "mare", "mari", "mark",
		"marr", "mart", "mary", "masc", "mash", "mask", "mass", "mast", "mate", "math",
		"matr", "matt", "matu", "maze", "mber", "mbin", "mble", "meal", "mean", "meas",
		"meat", "mech", "meda", "medi", "medy", "meet", "mega", "melo", "melt", "memo",
		"mend", "mens", "ment", "mera", "merc", "merg", "mess", "mest", "mete", "meth",
		"metr", "migr", "mile", "mili", "milk", "mill", "mind", "mine", "mini", "mint",
		"minu", "mira", "mirr", "misc", "mish", "miss", "mist", "mite", "mmer", "mmis",
		"mmit", "mode", "mold", "mond", "mone", "monk", "mont", "moon", "moor", "more",
		"morn", "mort", "mote", "moth", "moti", "moun", "move", "movi", "mpac", "mpar",
		"mpas", "mpat", "mper", "mple", "mule", "muni", "musc", "mush", "musi", "musk",
		"mute", "mutu", "myst", "myth", "nage", "nail", "nake", "nald", "name", "nate",
		"nati", "natu", "naut", "ncia", "ncil", "ndam", "nder", "ndic", "ndit", "ndle",
		"neck", "nect", "need", "nega", "nerg", "nest", "netw", "neut", "nfra", "ngag",
		"ngen", "nger", "ngin", "ngle", "ngra", "ngth", "nice", "nick", "nion", "nior",
		"nito", "nitr", "nkle", "nman", "nnel", "noon", "norm", "nose", "note", "noti",
		"noun", "nput", "nser", "nset", "nsio", "nsis", "nsit", "nsti", "nstr", "nsul",
		"nten", "nter", "ntin", "ntra", "ntro", "ntry", "numb", "nute", "oach", "oard",
		"oast", "obab", "ocen", "ocke", "octo", "ocus", "odel", "odge", "offe", "offi",
		"ogra", "oice", "oise", "oist", "olat", "ollo", "oman", "omba", "ombi", "omen",
		"omeo", "omet", "omma", "ommo", "ompa", "ompe", "ompl", "onar", "onat", "onde",
		"ondo", "ondu", "onen", "oney", "ongr", "onic", "onom", "onor", "onsc", "onst",
		"ontr", "oodl", "oose", "ooth", "open", "oper", "opin", "ople", "opti", "orge",
		"orig", "orit", "orni", "orri", "orth", "ortu", "osis", "osit", "ossi", "otch",
		"otel", "othe", "ottl", "oubl", "ouch", "ough", "ounc", "ound", "oung", "ount",
		"ourn", "ouse", "oust", "oute", "outh", "outl", "oval", "ovel", "oven", "over",
		"ower", "oxid", "oyal", "pace", "pack", "pact", "page", "pain", "pair", "pale",
		"pali", "pall", "palm", "pand", "pane", "pani", "pank", "pant", "para", "pare",
		"pari", "park", "parl", "part", "pass", "past", "pate", "path", "patr", "patt",
		"pave", "peak", "pear", "peck", "pect", "pede", "pell", "pena", "pend", "pene",
		"peni", "pent", "peop", "perc", "perf", "peri", "perm", "pers", "pert", "pess",
		"pest", "pete", "peti", "phan", "phas", "phil", "phin", "phon", "phor", "phos",
		"phra", "phys", "pick", "pict", "pike", "pill", "pine", "ping", "pipe", "pire",
		"pise", "pist", "plac", "plag", "plan", "plas", "plat", "play", "plet", "plex",
		"plic", "ploy", "plun", "pock", "poin", "pois", "poke", "pole", "poli", "poll",
		"pond", "pone", "pong", "pool", "poor", "popu", "pore", "pork", "porn", "port",
		"pose", "posi", "post", "pour", "ppea", "ppen", "pple", "ppli", "pray", "prea",
		"prec", "pred", "prem", "prep", "pres", "pret", "prev", "pric", "prim", "prin",
		"prio", "pris", "priv", "prob", "proc", "prof", "prom", "prop", "pros", "prot",
		"prov", "prox", "pter", "ptim", "publ", "pugn", "pull", "pump", "purg", "push",
		"puss", "pute", "quad", "quan", "quar", "quen", "quer", "ques", "quir", "quit",
		"quiz", "race", "rack", "ract", "rade", "radi", "raft", "rage", "raid", "rail",
		"rain", "rait", "rald", "rama", "ramb", "rame", "ramp", "ranc", "rand", "rang",
		"rank", "rans", "rape", "rash", "rass", "rast", "rate", "rath", "rati", "rato",
		"rave", "rbor", "rcen", "reac", "read", "reak", "real", "ream", "reap", "rear",
		"reas", "reat", "rebe", "rebo", "reca", "rece", "reck", "reco", "rect", "rede",
		"redi", "redu", "reed", "reet", "refe", "refo", "refu", "regi", "regu", "rehe",
		"rela", "rele", "reli", "reme", "remo", "rend", "rent", "repe", "repo", "repr",
		"repu", "resc", "resh", "resi", "reso", "resp", "ress", "rest", "resu", "reta",
		"retr", "retu", "reve", "rgen", "rget", "ribe", "rice", "rich", "rick", "rict",
		"ride", "rier", "riff", "rifl", "rift", "rike", "rill", "rime", "rina", "rine",
		"ring", "rink", "ripe", "ript", "rise", "rish", "risk", "rist", "rita", "rive",
		"rize", "rman", "rmor", "rnet", "road", "roar", "robe", "robo", "rock", "rode",
		"roga", "roid", "roil", "roke", "role", "roll", "roma", "romb", "romo", "rone",
		"rong", "roni", "ront", "rook", "room", "roon", "root", "rope", "roph", "rose",
		"ross", "roth", "roun", "roup", "rous", "rout", "rove", "rovi", "rown", "rran",
		"rren", "rres", "rror", "rrow", "rson", "rter", "rtun", "rude", "rule", "rumb",
		"rump", "rung", "rupt", "rush", "russ", "rust", "sack", "sacr", "sade", "safe",
		"sage", "sail", "sala", "sale", "sali", "salo", "salu", "salv", "sand", "sane",
		"sani", "sano", "sant", "sary", "sati", "scal", "scam", "scan", "scar", "scen",
		"sche", "scho", "scon", "scor", "scot", "scou", "scra", "scri", "scru", "scur",
		"scus", "seal", "sear", "seas", "seat", "sect", "secu", "sede", "seed", "sele",
		"self", "sell", "semi", "send", "seni", "sens", "sent", "sequ", "seri", "sers",
		"sert", "serv", "sess", "sett", "seve", "shad", "sham", "shan", "shap", "shar",
		"shea", "shee", "shel", "shie", "shin", "ship", "shir", "shoo", "shop", "shor",
		"shot", "shou", "show", "shut", "sick", "side", "sign", "sile", "silv", "simi",
		"simu", "sing", "sink", "sire", "sist", "site", "size", "skel", "sket", "skin",
		"slam", "slap", "slea", "slee", "slid", "slin", "slip", "slog", "slow", "sman",
		"snap", "snip", "snow", "soci", "sock", "soft", "sole", "solu", "solv", "some",
		"song", "soph", "sore", "sort", "soul", "sour", "spac", "span", "spar", "spat",
		"spec", "spee", "spel", "spen", "spin", "spir", "spit", "spla", "spli", "spok",
		"spon", "spoo", "spot", "spri", "spun", "spur", "sput", "squa", "sque", "squi",
		"ssen", "sset", "stab", "staf", "stag", "stai", "stak", "stal", "stam", "stan",
		"star", "stat", "stay", "stea", "stel", "stem", "sten", "step", "ster", "stic",
		"stif", "stig", "stit", "stle", "stoc", "stom", "ston", "stop", "stor", "stra",
		"stre", "stri", "stro", "stru", "stud", "subs", "succ", "suck", "suit", "sult",
		"sume", "summ", "sump", "sund", "supp", "supr", "sure", "surg", "surr", "surv",
		"susp", "swim", "symb", "synt", "tabl", "tach", "tack", "tact", "tail", "tain",
		"take", "tale", "tali", "talk", "tall", "tame", "tamp", "tang", "tank", "tard",
		"targ", "tase", "task", "tate", "taxi", "team", "tear", "tech", "tect", "teen",
		"tele", "tell", "temp", "tena", "tenc", "tend", "teno", "tens", "tent", "terc",
		"tere", "terf", "term", "tern", "terr", "terv", "test", "text", "thar", "them",
		"theo", "ther", "thin", "thol", "thon", "thor", "thre", "thro", "thun", "tice",
		"tick", "ticl", "tide", "tiff", "tige", "tile", "tilt", "timb", "time", "tina",
		"tine", "ting", "tink", "tinu", "tion", "tire", "tiss", "tita", "titu", "tman",
		"tock", "toil", "tole", "toll", "tomb", "tomo", "tone", "tono", "tool", "toon",
		"toot", "tore", "torr", "tort", "tory", "tour", "tout", "town", "tput", "trac",
		"trad", "trag", "trai", "tran", "trap", "trav", "tray", "trea", "tree", "trem",
		"tren", "tres", "tria", "trib", "tric", "trig", "trio", "trip", "trix", "trol",
		"tron", "trop", "trou", "troy", "truc", "true", "trum", "trun", "trus", "tter",
		"ttle", "tube", "tude", "tume", "tune", "turb", "ture", "turn", "tute", "twin",
		"type", "uard", "uate", "ubbl", "ubli", "ucle", "udge", "uenc", "uest", "ught",
		"uite", "ulat", "ulge", "ulle", "ulti", "umbl", "ummy", "umor", "umph", "unce",
		"unch", "uncl", "unct", "unde", "unge", "ungu", "unic", "unio", "unit", "uple",
		"urch", "uret", "urge", "urse", "ussi", "ustr", "utch", "utin", "vaca", "vacu",
		"vade", "vail", "vain", "vale", "valu", "vamp", "vani", "vant", "vate", "veal",
		"vect", "vehi", "vein", "velo", "vend", "vene", "veni", "vent", "verb", "verh",
		"veri", "vers", "vert", "vest", "vice", "vici", "vict", "vide", "view", "vile",
		"vill", "vinc", "vine", "viol", "virt", "visi", "vite", "vive", "voca", "void",
		"voke", "volt", "volu", "vore", "vote", "voup", "vour", "wait", "wake", "walk",
		"wall", "want", "ward", "ware", "warm", "warn", "warr", "wart", "wash", "wast",
		"wave", "weal", "wear", "weed", "week", "weep", "well", "west", "whip", "whis",
		"whol", "whop", "wick", "wide", "wife", "wild", "will", "wind", "wine", "wing",
		"wipe", "wire", "wise", "wish", "wist", "wolf", "word", "work", "worl", "worm",
		"worn", "wort", "wrap", "writ", "xper", "xplo", "xtra", "yard", "yarn", "year",
		"ymph", "yoff", "yond", "youn", "yout", "zero", "zest", "zone", "zoom", "zzle"
	};

	private static final String[] l5g = {
		"actic", "actor", "adult", "advan", "agent", "agree", "alleg", "allow", "ament", "ameri",
		"anger", "anima", "ankle", "apply", "arden", "assoc", "atern", "ation", "attle", "audio",
		"austr", "avail", "award", "battl", "beach", "beard", "belly", "black", "blaze", "blend",
		"block", "blood", "board", "brack", "bread", "break", "brick", "broad", "brook", "broth",
		"brown", "brute", "build", "busin", "cargo", "ceive", "centr", "centu", "chair", "champ",
		"chanc", "chang", "chant", "chara", "charg", "chase", "cheap", "check", "cheek", "cheer",
		"chees", "cheme", "chest", "chief", "child", "circl", "circu", "claim", "class", "clean",
		"clear", "clerk", "cliff", "cline", "clock", "close", "cloth", "clown", "clude", "coast",
		"colon", "comma", "commo", "compa", "compe", "confe", "confi", "conne", "const", "consu",
		"conta", "conte", "contr", "conve", "counc", "count", "cours", "court", "cover", "craft",
		"crash", "craze", "creas", "creat", "creek", "cresc", "crest", "crime", "cross", "crown",
		"crude", "crush", "crust", "cture", "curre", "custo", "cycle", "dairy", "decid", "defen",
		"democ", "depar", "depen", "desig", "devel", "direc", "disco", "distr", "draft", "dress",
		"drift", "drink", "drive", "dynam", "ealth", "earin", "earth", "eason", "easur", "educa",
		"egion", "egree", "eight", "elbow", "elect", "elite", "ellow", "emand", "ember", "emplo",
		"energ", "enjoy", "enter", "eport", "equen", "equip", "ermin", "escri", "estab", "estig",
		"estim", "event", "exert", "expec", "exten", "famil", "fashi", "fathe", "featu", "felon",
		"fever", "field", "fight", "figur", "final", "finan", "finge", "flash", "flavo", "flesh",
		"flict", "flush", "force", "fores", "forge", "found", "frame", "fraud", "frien", "front",
		"frown", "fruit", "futur", "gener", "ghost", "glass", "globe", "gover", "grace", "grade",
		"grain", "grand", "grant", "graph", "green", "greet", "gress", "grind", "groom", "groun",
		"grove", "guard", "guest", "guile", "hance", "happe", "happy", "haven", "heart", "heath",
		"highw", "histo", "hollo", "horse", "hotel", "house", "human", "icate", "icide", "icipa",
		"iddle", "ident", "imate", "impor", "index", "indus", "iness", "infor", "insta", "instr",
		"integ", "inter", "intro", "inves", "islan", "issue", "isten", "janit", "joint", "judge",
		"junct", "jungl", "knife", "knuck", "larce", "large", "later", "laugh", "learn", "lease",
		"level", "light", "limit", "litic", "llage", "llege", "lleng", "llion", "locat", "lunch",
		"macro", "manag", "manor", "march", "marke", "marsh", "mason", "maste", "mater", "matio",
		"meado", "media", "medic", "membe", "merge", "meter", "micro", "might", "milit", "milli",
		"minat", "minut", "mmuni", "model", "money", "month", "motor", "mount", "movie", "music",
		"nance", "natio", "natur", "neigh", "neral", "neutr", "ngine", "night", "nsist", "ntern",
		"nurse", "ocean", "offic", "ology", "ommun", "ontra", "opera", "orbit", "order", "organ",
		"otent", "other", "ouble", "ounce", "paign", "paper", "paren", "party", "pater", "peace",
		"peopl", "perio", "phase", "phone", "photo", "pital", "place", "plain", "plane", "plant",
		"plate", "plent", "plete", "plica", "plore", "point", "polic", "polit", "posit", "pound",
		"power", "ppear", "pport", "pract", "prehe", "press", "price", "prima", "princ", "pring",
		"prise", "proba", "proce", "produ", "progr", "proje", "prone", "prope", "prosp", "prote",
		"proto", "prove", "publi", "pulse", "quart", "queer", "quest", "queue", "quick", "quire",
		"radio", "raise", "ranch", "range", "reach", "recom", "recor", "reduc", "refer", "refor",
		"regul", "rench", "repla", "repor", "repub", "resid", "ridge", "right", "river", "roduc",
		"round", "scale", "scape", "schoo", "scien", "score", "screw", "searc", "seaso", "secur",
		"senat", "sense", "shack", "shake", "share", "shark", "sheep", "sheet", "shell", "shine",
		"shire", "shirt", "shore", "short", "sight", "simil", "skate", "skill", "skull", "sland",
		"slate", "smart", "smash", "smile", "smith", "smoke", "socia", "solve", "sound", "sourc",
		"space", "speak", "spear", "spect", "spend", "spill", "spire", "spite", "splay", "spoil",
		"sport", "sprin", "squad", "squar", "ssist", "stabl", "stack", "staff", "stain", "stake",
		"stall", "stamp", "stand", "start", "state", "stati", "stead", "steal", "steam", "steel",
		"steer", "stick", "still", "stock", "stone", "store", "storm", "story", "strai", "stran",
		"strat", "stree", "strik", "strip", "struc", "stuff", "style", "subst", "super", "sweet",
		"sword", "table", "taste", "teach", "techn", "tempt", "teria", "terra", "thank", "theat",
		"theor", "therm", "think", "tight", "title", "toast", "tongu", "tooth", "tower", "toxic",
		"track", "tract", "trade", "trail", "train", "trans", "trave", "treat", "treme", "trend",
		"trial", "trict", "troll", "trong", "truck", "truct", "trust", "tutor", "ublic", "ultur",
		"ument", "under", "unive", "ustle", "valle", "value", "velop", "venue", "veter", "video",
		"villa", "ville", "vista", "volve", "waist", "waste", "watch", "water", "whack", "white",
		"winte", "woman", "world", "worth", "wound", "wreck", "wrist", "write", "yield", "ystem"
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
