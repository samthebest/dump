#!/usr/bin/env python3
"""Render the review-review report from a combined.json.

Usage: build_report.py <combined.json> <out.html>

Expected combined.json schema:
{
  "meta": {"scope": str, "total": int, "date": str, "sources": [str]},
  "counts": {"byCluster": {cluster:int}, "byRestaurant": {name:{cluster:int}}, "bySource": {src:int}},
  "summary": {"dismissible":int,"credible_negative":int,"positive":int,"rating_only":int,"mixed":int},
  "restaurants": [{"name":str,"score_label":str,"total":int,"badge":str,"take":str}],  # score_label e.g. "Google 4.5*"
  "histogram": {"years":[int], "overall":{year:{cluster:int}}, "byRestaurant":{name:{year:{cluster:int}}}},
  "quotes": [{"reviewer":str,"restaurant":str,"year":int,"rating":int,"text":str,"cluster":str,"source":str}]
}
Per-restaurant ranking score is computed in-browser from counts.byRestaurant (transparent + deterministic).
"""
import json, sys

src = sys.argv[1] if len(sys.argv) > 1 else "combined.json"
out = sys.argv[2] if len(sys.argv) > 2 else "report.html"
d = json.load(open(src))
payload = json.dumps(d)

HTML = r'''<title>__TITLE__</title>
<style>
:root{
  --ground:#F4EBD6; --paper:#FBF5E6; --ink:#211B14; --muted:#6E6147;
  --red:#BE1622; --jade:#1C6B55; --jade-soft:#7FB39E; --gold:#C2962C; --clay:#C9743F; --taupe:#8C8275;
  --line:rgba(33,27,20,.16);
  --c-non_food_complaint:#8C8275;
  --c-unsophisticated_complaint:#C9743F;
  --c-mixed_or_other:#CDC2A6;
  --c-rating_only:#9AA7A0;
  --c-credible_food_complaint:#C2962C;
  --c-generic_praise:#7FB39E;
  --c-authentic_praise:#1C6B55;
}
*{box-sizing:border-box}
.wrap{background:var(--ground);color:var(--ink);
  font-family:Georgia,"Iowan Old Style","Times New Roman",serif;line-height:1.55;overflow-x:hidden}
.disp{font-family:"Arial Narrow","Helvetica Neue Condensed","Roboto Condensed","Helvetica Neue",Arial,sans-serif;
  text-transform:uppercase;font-weight:700;letter-spacing:.04em;line-height:.98}
.shell{max-width:1080px;margin:0 auto;padding:0 24px}

.hero{background:var(--red);color:#FBF1DA;position:relative;overflow:hidden;border-bottom:5px solid var(--gold)}
.hero::before{content:"";position:absolute;inset:0;opacity:.10;
  background:repeating-linear-gradient(135deg,#000 0 2px,transparent 2px 22px)}
.hero .shell{position:relative;padding:54px 24px 56px}
.eyebrow{font-family:ui-monospace,Menlo,monospace;font-size:12px;letter-spacing:.30em;text-transform:uppercase;color:#F2C9B0;margin:0 0 18px}
.hero h1{margin:0;font-size:clamp(42px,8vw,92px)}
.hero h1 .b{color:var(--gold)}
.thesis{font-size:clamp(16px,2.1vw,20px);max-width:680px;margin:22px 0 0;color:#FAE9D2}
.seal{position:absolute;top:38px;right:26px;width:96px;height:96px;border-radius:14px;border:3px solid var(--gold);
  display:flex;align-items:center;justify-content:center;transform:rotate(8deg);color:var(--gold);text-align:center;background:rgba(0,0,0,.10)}
.seal span{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:13px;letter-spacing:.10em;line-height:1.05;text-transform:uppercase}
.filter{display:flex;flex-wrap:wrap;gap:0;margin-top:36px;border:1px solid rgba(255,255,255,.35)}
.fstep{flex:1 1 0;min-width:148px;padding:18px 20px;border-right:1px solid rgba(255,255,255,.25)}
.fstep:last-child{border-right:0}
.fstep .n{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:clamp(34px,5.2vw,52px);line-height:1}
.fstep .l{font-size:13px;color:#F6DCC4;margin-top:6px}
.fstep.keep{background:rgba(28,107,85,.30)} .fstep.keep .n{color:#BFE8D6}
.hnote{font-size:13px;color:#F0C7AE;margin:14px 0 0}

.sec{padding:54px 0}
.sec--paper{background:var(--paper);border-top:1px solid var(--line);border-bottom:1px solid var(--line)}
.kicker{font-family:ui-monospace,Menlo,monospace;font-size:12px;letter-spacing:.26em;text-transform:uppercase;color:var(--red);margin:0 0 10px}
h2.disp{font-size:clamp(28px,4.2vw,42px);margin:0 0 8px;color:var(--ink)}
.lede{font-size:17px;color:var(--muted);max-width:720px;margin:0 0 28px}
p{margin:0 0 14px}

.cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:14px}
.card{background:#fff;border:1px solid var(--line);border-radius:10px;padding:18px 20px}
.card .big{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:40px;line-height:1;color:var(--red)}
.card .cap{font-size:13.5px;color:var(--muted);margin-top:8px}
.note{font-size:13.5px;color:var(--muted);border-left:3px solid var(--gold);padding:6px 0 6px 14px;margin:24px 0 0;max-width:800px}

.rub{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:14px;margin-top:8px}
.rcard{border:1px solid var(--line);border-radius:10px;padding:16px 18px;background:#fff;border-top:5px solid var(--c)}
.rcard .tag{font-family:ui-monospace,Menlo,monospace;font-size:11px;letter-spacing:.10em;text-transform:uppercase;color:#fff;background:var(--c);padding:3px 8px;border-radius:4px;display:inline-block}
.rcard h3{font-family:Georgia,serif;font-size:18px;margin:12px 0 6px}
.rcard p{font-size:14px;color:var(--muted);margin:0}
.verdict{font-family:"Arial Narrow",sans-serif;font-weight:700;text-transform:uppercase;letter-spacing:.06em;font-size:13px;margin-top:12px}
.v-dismiss{color:var(--clay)} .v-keep{color:var(--gold)} .v-good{color:var(--jade)} .v-neutral{color:var(--taupe)}

.legend{display:flex;flex-wrap:wrap;gap:13px 20px;margin:0 0 24px;font-size:13.5px}
.legend .it{display:flex;align-items:center;gap:8px}
.sw{width:14px;height:14px;border-radius:3px;flex:none}

.hbars{display:flex;flex-direction:column;gap:11px;margin-top:8px}
.hrow{display:grid;grid-template-columns:250px 1fr 56px;align-items:center;gap:14px}
.hrow .lab{font-size:14px}
.hrow .track{background:rgba(33,27,20,.07);border-radius:6px;height:26px;overflow:hidden}
.hrow .fill{height:100%;border-radius:6px}
.hrow .val{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:22px;text-align:right}
@media(max-width:560px){.hrow{grid-template-columns:1fr 50px;grid-template-rows:auto auto}.hrow .track{grid-column:1/3}}

/* leaderboard */
.lb{display:flex;flex-direction:column;gap:8px;margin:0 0 8px}
.lbrow{display:grid;grid-template-columns:34px 1fr 150px 52px;align-items:center;gap:14px;
  background:#fff;border:1px solid var(--line);border-radius:9px;padding:11px 16px}
.lbrow .rk{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:26px;color:var(--muted)}
.lbrow.top .rk{color:var(--gold)}
.lbrow .nm{font-family:"Arial Narrow",sans-serif;text-transform:uppercase;letter-spacing:.03em;font-weight:700;font-size:19px}
.lbrow .nm small{display:block;font-family:ui-monospace,Menlo,monospace;font-size:11px;letter-spacing:0;text-transform:none;color:var(--muted);font-weight:400;margin-top:2px}
.lbrow .sbar{background:rgba(33,27,20,.07);border-radius:5px;height:12px;overflow:hidden}
.lbrow .sbar i{display:block;height:100%;background:var(--jade);border-radius:5px}
.lbrow .sc{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:24px;text-align:right}
@media(max-width:560px){.lbrow{grid-template-columns:28px 1fr 44px}.lbrow .sbar{display:none}}

/* vertical restaurant detail cards */
.rest{display:flex;flex-direction:column;gap:18px;margin-top:8px}
.rcd{background:#fff;border:1px solid var(--line);border-radius:12px;padding:22px;border-left:7px solid var(--accent)}
.rcd .rhead{display:flex;align-items:baseline;gap:12px;flex-wrap:wrap}
.rcd h3.disp{font-size:28px;margin:0}
.rcd .rk2{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:18px;color:var(--gold)}
.rcd .meta{font-family:ui-monospace,Menlo,monospace;font-size:12px;color:var(--muted);margin:6px 0 14px}
.rcd-body{display:grid;grid-template-columns:1fr;gap:20px}
@media(min-width:780px){.rcd-body{grid-template-columns:1.05fr 1fr}}
.composite{display:flex;height:28px;border-radius:6px;overflow:hidden;margin-bottom:6px}
.composite i{display:block;height:100%}
.ctot{font-size:12px;color:var(--muted);margin-bottom:14px}
.take{font-size:14.5px;color:var(--ink);margin-bottom:12px}
.badge{display:inline-block;font-family:"Arial Narrow",sans-serif;font-weight:700;text-transform:uppercase;letter-spacing:.05em;font-size:12px;padding:4px 9px;border-radius:5px;margin-bottom:12px}
.rquote{border-left:4px solid var(--c);padding:4px 0 4px 12px;font-size:13.5px;color:var(--muted);font-style:italic}
.rquote .qm{font-family:ui-monospace,Menlo,monospace;font-size:11px;font-style:normal;color:var(--muted);display:block;margin-top:5px}

.hwrap{overflow-x:auto;padding-bottom:6px}
.bars{display:flex;gap:7px;align-items:flex-end;min-height:200px;border-bottom:2px solid var(--ink);padding-top:8px}
.bars.wide{min-width:620px;min-height:258px}
.bars.card-bars{min-width:340px;min-height:158px}
.col{flex:1 1 0;min-width:22px;display:flex;flex-direction:column;align-items:center;gap:5px}
.stack{width:100%;max-width:42px;display:flex;flex-direction:column-reverse}
.stack i{display:block;width:100%}
.stack i:first-child{border-radius:0 0 3px 3px}.stack i:last-child{border-radius:3px 3px 0 0}
.yr{font-family:ui-monospace,Menlo,monospace;font-size:10.5px;color:var(--muted);writing-mode:vertical-rl;transform:rotate(180deg)}
.htot{font-family:"Arial Narrow",sans-serif;font-weight:700;font-size:12px;color:var(--ink)}
.charttitle{font-family:"Arial Narrow",sans-serif;text-transform:uppercase;letter-spacing:.04em;font-size:15px;color:var(--muted);margin:0 0 6px}

.recgroup h3{font-family:"Arial Narrow",sans-serif;text-transform:uppercase;letter-spacing:.05em;font-size:22px;margin:32px 0 4px}
.recgroup .gsub{font-size:14px;color:var(--muted);margin:0 0 16px}
.quotes{display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:14px}
.q{background:#fff;border:1px solid var(--line);border-radius:10px;padding:16px 18px;border-left:5px solid var(--c)}
.q .stars{color:var(--red);font-size:13px;letter-spacing:1px}
.q blockquote{margin:8px 0 12px;font-size:14.5px;line-height:1.5}
.q .src{font-family:ui-monospace,Menlo,monospace;font-size:11.5px;color:var(--muted);display:flex;justify-content:space-between;gap:8px;border-top:1px dashed var(--line);padding-top:8px;flex-wrap:wrap}
.q .who{font-weight:700;color:var(--ink)}

.bottom{background:var(--ink);color:#F1E6CF;border-top:5px solid var(--gold)}
.bottom .shell{padding:52px 24px}
.bottom h2{color:#fff} .bottom .lede{color:#C9BC9F}
.foot{font-family:ui-monospace,Menlo,monospace;font-size:11.5px;color:var(--muted);padding:26px 0;text-align:center}
a{color:var(--red)}
:focus-visible{outline:3px solid var(--gold);outline-offset:2px}
@media(prefers-reduced-motion:reduce){*{animation:none!important;transition:none!important}}
</style>

<div class="wrap">
  <header class="hero">
    <div class="shell">
      <p class="eyebrow" id="eyebrow"></p>
      <div class="seal"><span id="seal"></span></div>
      <h1 class="disp">The Bad Reviews<br>Are Mostly <span class="b">Noise</span></h1>
      <p class="thesis" id="thesis"></p>
      <div class="filter" id="filter"></div>
      <p class="hnote" id="hnote"></p>
    </div>
  </header>

  <section class="sec sec--paper">
    <div class="shell">
      <p class="kicker">01 &middot; The verdict</p>
      <h2 class="disp">Ranked by real quality &amp; authenticity</h2>
      <p class="lede">Score rewards praise from discerning diners and penalises credible food faults; delivery gripes, price, and unsophisticated-palate complaints are discounted. See &ldquo;how we score&rdquo; below.</p>
      <div class="lb" id="leaderboard"></div>
      <p class="note" id="scorenote"></p>
    </div>
  </section>

  <section class="sec">
    <div class="shell">
      <p class="kicker">02 &middot; What we read &amp; how</p>
      <h2 class="disp">Coverage &amp; honest caveats</h2>
      <p class="lede">Every Google and TripAdvisor review, read in full &mdash; nothing cherry-picked, every review classified by a full-strength agent (no sampling, no keyword shortcuts).</p>
      <div class="cards" id="coverage"></div>
      <p class="note" id="caveat"></p>
    </div>
  </section>

  <section class="sec sec--paper">
    <div class="shell">
      <p class="kicker">03 &middot; The sieve</p>
      <h2 class="disp">Six judgements, three questions</h2>
      <p class="lede">Irrelevant non-food gripes, unsophisticated-palate complaints, and praise from people who order authentically &mdash; plus the negatives that <em>are</em> worth heeding, and praise that&rsquo;s nice but not discerning. Star-only ratings (no text) sit apart.</p>
      <div class="rub" id="rubric"></div>
    </div>
  </section>

  <section class="sec">
    <div class="shell">
      <p class="kicker">04 &middot; The totals</p>
      <h2 class="disp">Everything, in one stack</h2>
      <p class="lede">All reviews across every outlet, sorted from noise (top) toward signal (bottom).</p>
      <div class="legend" id="legend"></div>
      <div class="hbars" id="totals"></div>
    </div>
  </section>

  <section class="sec sec--paper">
    <div class="shell">
      <p class="kicker">05 &middot; Outlet by outlet</p>
      <h2 class="disp">The detail, ranked</h2>
      <p class="lede">Each outlet&rsquo;s review mix and its history year by year. Bar height = reviews that year; colour = what kind.</p>
      <div class="legend" id="legend2"></div>
      <div class="rest" id="restaurants"></div>
    </div>
  </section>

  <section class="sec">
    <div class="shell">
      <p class="kicker">06 &middot; The trend overall</p>
      <h2 class="disp">Were they bad, are they good now?</h2>
      <p class="lede">All outlets combined. Watch the gold (credible food faults) and deep-jade (authentic praise) over time.</p>
      <div class="hwrap"><div class="bars wide" id="histAll"></div></div>
    </div>
  </section>

  <section class="sec sec--paper">
    <div class="shell">
      <p class="kicker">07 &middot; The receipts</p>
      <h2 class="disp">Verbatim, so you can judge for yourself</h2>
      <p class="lede">Real reviews from each pile, across both platforms.</p>
      <div id="receipts"></div>
    </div>
  </section>

  <section class="bottom">
    <div class="shell">
      <p class="kicker" style="color:var(--gold)">08 &middot; Bottom line</p>
      <h2 class="disp">After the noise is gone</h2>
      <p class="lede" id="bottomlede"></p>
    </div>
    <p class="foot" id="foot"></p>
  </section>
</div>

<script>
const D = __PAYLOAD__;
const CONTENT = [
  ["non_food_complaint","Non-food gripe","delivery, wait, price, wrong order, rude staff"],
  ["unsophisticated_complaint","Unsophisticated palate","judged by anglicised takeaway staples — chips, chicken balls, “not spicy/bland”"],
  ["mixed_or_other","Mixed / unclear","good food + a non-food gripe, or genuinely split"],
  ["credible_food_complaint","Credible food complaint","raw/undercooked, illness, foreign objects, hygiene, real recipe faults"],
  ["generic_praise","Generic praise","“lovely”, “best in town”, hot & tasty, good value"],
  ["authentic_praise","Authentic-literate praise","traditional / regional / off-menu dishes judged on their own terms"],
];
const RATING_ONLY = ["rating_only","Star-only rating","a bare star score with no written text"];
const ALL = CONTENT.concat([RATING_ONLY]);
const LABEL = Object.fromEntries(ALL.map(c=>[c[0],c[1]]));
const COLOR = k => getComputedStyle(document.documentElement).getPropertyValue('--c-'+k).trim();
const STACK = ["non_food_complaint","unsophisticated_complaint","mixed_or_other","rating_only","credible_food_complaint","generic_praise","authentic_praise"];
const el=(t,c,h)=>{const e=document.createElement(t);if(c)e.className=c;if(h!=null)e.innerHTML=h;return e;};
const esc=s=>(s||'').replace(/</g,'&lt;').replace(/<br>/gi,' ');

function scoreOf(by){
  const A=by.authentic_praise||0,G=by.generic_praise||0,C=by.credible_food_complaint||0,U=by.unsophisticated_complaint||0;
  const num=2*A+G-2*C-0.5*U, den=2*A+G+2*C+0.5*U;
  return den>0 ? Math.round((num/den+1)/2*100) : 50;
}
const ranked = D.restaurants.slice().map(r=>({...r, score:scoreOf(D.counts.byRestaurant[r.name]||{}),
  by:D.counts.byRestaurant[r.name]||{}})).sort((a,b)=>b.score-a.score);

/* hero */
document.getElementById('eyebrow').textContent = 'Review forensics · ' + D.meta.scope;
document.getElementById('seal').innerHTML = 'De-noised<br>'+D.meta.total+'<br>reviews';
document.getElementById('thesis').innerHTML = 'Every Google and TripAdvisor review for '+D.restaurants.length+' outlets ('+D.meta.scope+'), read end to end. Strip the complaints that are really about delivery vans and unsophisticated palates, and the credible food signal — good and bad — stands out, outlet by outlet.';
(function(){
  const s=D.summary, f=document.getElementById('filter');
  [[D.meta.total,"reviews read",""],[s.dismissible,"binned as noise",""],[s.credible_negative,"credible food faults",""],[s.positive,"positive on the food","keep"]]
   .forEach(([n,l,k])=>{const x=el('div','fstep'+(k?' '+k:''));x.innerHTML='<div class="n">'+n+'</div><div class="l">'+l+'</div>';f.appendChild(x);});
  document.getElementById('hnote').innerHTML='&plus; '+s.rating_only+' star-only ratings (no text) and '+s.mixed+' genuinely mixed.';
})();

/* leaderboard */
(function(){
  const lb=document.getElementById('leaderboard'), max=Math.max(...ranked.map(r=>r.score),1);
  ranked.forEach((r,i)=>{
    const row=el('div','lbrow'+(i===0?' top':''));
    const A=r.by.authentic_praise||0,C=r.by.credible_food_complaint||0;
    row.innerHTML='<div class="rk">'+(i+1)+'</div>'+
      '<div class="nm">'+r.name+'<small>'+(r.score_label||'')+' · '+A+' authentic raves · '+C+' credible faults · '+r.total+' reviews</small></div>'+
      '<div class="sbar"><i style="width:'+(r.score/max*100)+'%"></i></div>'+
      '<div class="sc">'+r.score+'</div>';
    lb.appendChild(row);
  });
  document.getElementById('scorenote').innerHTML='<strong>How we score (0–100).</strong> index = (2·authentic + generic − 2·credible − ½·unsophisticated) over the same terms&rsquo; magnitudes, rescaled to 0–100. Authentic-literate praise counts double; credible food faults count double against; delivery/price/wait and star-only ratings are excluded; unsophisticated-palate complaints are heavily discounted. Small review counts are noisier — weigh the raw counts too.';
})();

/* coverage */
(function(){
  const c=document.getElementById('coverage'), bs=D.counts.bySource;
  const cards=[[D.meta.total,"reviews read & classified"]];
  Object.keys(bs).forEach(k=>cards.push([bs[k], k+" reviews"]));
  cards.forEach(([n,cap])=>{const x=el('div','card');x.innerHTML='<div class="big">'+n+'</div><div class="cap">'+cap+'</div>';c.appendChild(x);});
  document.getElementById('caveat').innerHTML='<strong>Read before trusting the totals.</strong> Reviews were pulled via the Apify Google-Maps and TripAdvisor scrapers using each platform&rsquo;s own timestamps, so the year histograms are reliable. Star-only ratings carry no text and are bucketed separately (counted for volume, excluded from the praise-vs-complaint split). Every review with text was classified by a full-strength agent against a fixed rubric — no sampling, no ML shortcut — but it is still automated judgement, so read each cluster as &ldquo;~this many&rdquo;.';
})();

/* rubric */
(function(){
  const map={non_food_complaint:["v-dismiss","Bin it"],unsophisticated_complaint:["v-dismiss","Bin it"],
    mixed_or_other:["v-neutral","Set aside"],credible_food_complaint:["v-keep","Heed it"],
    generic_praise:["v-good","Positive"],authentic_praise:["v-good","Trust it"]};
  const r=document.getElementById('rubric');
  CONTENT.forEach(([k,name,desc])=>{const d=el('div','rcard');d.style.setProperty('--c',COLOR(k));const v=map[k];
    d.innerHTML='<span class="tag">'+(D.counts.byCluster[k]||0)+' reviews</span><h3>'+name+'</h3><p>'+desc+'</p><div class="verdict '+v[0]+'">&rarr; '+v[1]+'</div>';r.appendChild(d);});
})();

/* legends */
function legend(id){const L=document.getElementById(id);ALL.forEach(([k,name])=>{const it=el('div','it');it.innerHTML='<span class="sw" style="background:'+COLOR(k)+'"></span>'+name;L.appendChild(it);});}
legend('legend'); legend('legend2');

/* totals */
(function(){
  const by=D.counts.byCluster, max=Math.max(...STACK.map(k=>by[k]||0),1), t=document.getElementById('totals');
  STACK.slice().reverse().forEach(k=>{const v=by[k]||0,row=el('div','hrow');
    row.innerHTML='<div class="lab">'+LABEL[k]+'</div><div class="track"><div class="fill" style="width:'+(v/max*100)+'%;background:'+COLOR(k)+'"></div></div><div class="val">'+v+'</div>';t.appendChild(row);});
})();

/* histogram builder */
function buildHist(container, perYear, years){
  let max=0; years.forEach(y=>{const t=STACK.reduce((a,k)=>a+((perYear[y]||{})[k]||0),0);if(t>max)max=t;});
  const H = container.classList.contains('wide')?226:150;
  years.forEach(y=>{const yc=perYear[y]||{}, tot=STACK.reduce((a,k)=>a+(yc[k]||0),0);
    const col=el('div','col'); const stack=el('div','stack'); stack.style.height=(max?tot/max*H:0)+'px';
    STACK.forEach(k=>{const v=yc[k]||0;if(v){const seg=el('i');seg.style.height=(v/tot*100)+'%';seg.style.background=COLOR(k);seg.title=y+' — '+LABEL[k]+': '+v;stack.appendChild(seg);}});
    col.appendChild(el('div','htot',tot||'')); col.appendChild(stack); col.appendChild(el('div','yr',y)); container.appendChild(col);});
}

/* restaurant detail cards (vertical, ranked) */
(function(){
  const wrap=document.getElementById('restaurants');
  ranked.forEach((r,i)=>{
    const by=r.by, tot=STACK.reduce((a,k)=>a+(by[k]||0),0);
    const card=el('div','rcd'); card.style.setProperty('--accent', i===0?'var(--gold)':'var(--red)');
    let comp=''; STACK.forEach(k=>{if(by[k])comp+='<i style="width:'+(by[k]/tot*100)+'%;background:'+COLOR(k)+'" title="'+LABEL[k]+': '+by[k]+'"></i>';});
    const A=by.authentic_praise||0,C=by.credible_food_complaint||0,pos=(by.authentic_praise||0)+(by.generic_praise||0),dis=(by.non_food_complaint||0)+(by.unsophisticated_complaint||0);
    const head='<div class="rhead"><span class="rk2">#'+(i+1)+'</span><h3 class="disp">'+r.name+'</h3><span class="rk2">score '+r.score+'</span></div>'+
      '<div class="meta">'+tot+' reviews · '+(r.score_label||'')+' · '+pos+' positive · '+C+' credible neg · '+dis+' noise</div>';
    const q=(D.quotes||[]).filter(x=>x.restaurant===r.name).sort((a,b)=>({authentic_praise:0,credible_food_complaint:1}[a.cluster]??2)-({authentic_praise:0,credible_food_complaint:1}[b.cluster]??2))[0];
    const left='<div>'+(r.badge?'<span class="badge" style="background:'+(i===0?'var(--gold)':'var(--red)')+'1f;color:'+(i===0?'var(--gold)':'var(--red)')+'">'+r.badge+'</span>':'')+
      '<div class="composite">'+comp+'</div><div class="ctot">noise → signal, left to right</div>'+
      '<div class="take">'+(r.take||'')+'</div>'+
      (q?'<div class="rquote" style="--c:'+COLOR(q.cluster)+'">“'+esc(q.text).slice(0,200)+'”<span class="qm">'+q.reviewer+' · '+q.year+' · '+q.source+'</span></div>':'')+'</div>';
    const histYears=Object.keys(D.histogram.byRestaurant[r.name]||{}).map(Number).sort((a,b)=>a-b);
    const right='<div><p class="charttitle">Reviews by year</p><div class="hwrap"><div class="bars card-bars" id="rh'+i+'"></div></div></div>';
    card.innerHTML=head+'<div class="rcd-body">'+left+right+'</div>';
    wrap.appendChild(card);
    buildHist(document.getElementById('rh'+i), D.histogram.byRestaurant[r.name]||{}, histYears);
  });
})();

buildHist(document.getElementById('histAll'), D.histogram.overall, D.histogram.years);

/* receipts */
(function(){
  const groups=[
    ["Bin these — not about the cooking","Non-food gripes and unsophisticated-palate complaints. The 1-stars to discount.",["non_food_complaint","unsophisticated_complaint"]],
    ["Keep these — real food faults","Concrete, legitimate defects: undercooking, illness, foreign objects, recipe faults.",["credible_food_complaint"]],
    ["The real praise — people who order authentically","Reviewers naming traditional, regional or off-menu dishes and judging them on their own terms.",["authentic_praise"]],
  ];
  const root=document.getElementById('receipts');
  groups.forEach(([h,sub,keys])=>{
    const sec=el('div','recgroup'); sec.innerHTML='<h3>'+h+'</h3><p class="gsub">'+sub+'</p>';
    const qg=el('div','quotes');
    (D.quotes||[]).filter(q=>keys.includes(q.cluster)).forEach(q=>{const card=el('div','q');card.style.setProperty('--c',COLOR(q.cluster));
      const stars='★'.repeat(q.rating)+'☆'.repeat(5-q.rating);
      card.innerHTML='<div class="stars">'+stars+'</div><blockquote>“'+esc(q.text)+'”</blockquote><div class="src"><span class="who">'+q.reviewer+'</span><span>'+q.restaurant+' · '+q.year+' · '+q.source+'</span></div>';qg.appendChild(card);});
    sec.appendChild(qg); root.appendChild(sec);
  });
})();

/* bottom + foot */
(function(){
  const s=D.summary, top=ranked[0];
  document.getElementById('bottomlede').innerHTML='Of '+D.meta.total+' reviews, only <strong>'+s.credible_negative+'</strong> are credible complaints about the food itself. '+s.dismissible+' are dismissible noise, '+s.positive+' are positive on the food, '+s.rating_only+' are star-only ratings. On authentic quality the pick is <strong>'+top.name+'</strong> (score '+top.score+'). The bad 1-stars are real — they just don’t say much about whether a kitchen can cook.';
  document.getElementById('foot').innerHTML='Sources: '+(D.meta.sources||['Google Maps','TripAdvisor']).join(' + ')+'. '+D.meta.total+' reviews, classified '+D.meta.date+'. Every review read by a full-strength agent; classification AI-assisted.';
})();
</script>'''

title = "Review Review — " + d.get("meta", {}).get("scope", "Restaurant audit")
html = HTML.replace("__PAYLOAD__", payload).replace("__TITLE__", title)
open(out, "w").write(html)
print("wrote", out, len(html), "bytes")
