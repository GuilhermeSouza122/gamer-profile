import { useEffect, useMemo, useState } from 'react'
import './App.css'
import './sync.css'

const API = 'http://localhost:8080/api/v1'

function App() {
  const [showLogin, setShowLogin] = useState(() => !new URLSearchParams(window.location.search).has('connected'))
  const [games, setGames] = useState([])
  const [selectedGame, setSelectedGame] = useState(null)
  const [achievements, setAchievements] = useState([])
  const [query, setQuery] = useState('')
  const [loading, setLoading] = useState(true)
  const [loadingAchievements, setLoadingAchievements] = useState(false)
  const [error, setError] = useState('')
  const [summary, setSummary] = useState(null)
  const [syncing, setSyncing] = useState(false)
  const [syncMessage, setSyncMessage] = useState('')

  useEffect(() => {
    fetch(`${API}/games`).then((response) => {
      if (!response.ok) throw new Error('Não foi possível carregar os jogos.')
      return response.json()
    }).then((data) => { setGames(data); if (data.length) selectGame(data[0]) })
      .catch((reason) => setError(reason.message)).finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    fetch(`${API}/dashboard/summary`).then((response) => response.json()).then(setSummary).catch(() => {})
  }, [games])

  function selectGame(game) {
    setSelectedGame(game); setLoadingAchievements(true)
    fetch(`${API}/games/${game.id}/achievements`).then((response) => response.json())
      .then(setAchievements).catch(() => setAchievements([])).finally(() => setLoadingAchievements(false))
  }

  async function synchronizeSteam() {
    setSyncing(true)
    setSyncMessage('Sincronizando biblioteca Steam...')
    setError('')
    try {
      const gamesResponse = await fetch(`${API}/integrations/steam/games/sync`, { method: 'POST' })
      if (!gamesResponse.ok) throw new Error('Não foi possível sincronizar os jogos.')
      const gamesResult = await gamesResponse.json()
      setSyncMessage(`Importando conquistas de ${gamesResult.games} jogos...`)
      const achievementsResponse = await fetch(`${API}/integrations/steam/games/achievements/sync-all`, { method: 'POST' })
      if (!achievementsResponse.ok) throw new Error('Jogos sincronizados, mas as conquistas falharam.')
      const achievementsResult = await achievementsResponse.json()
      const refreshedGames = await fetch(`${API}/games`).then((response) => response.json())
      setGames(refreshedGames)
      const refreshedSelected = refreshedGames.find((game) => game.id === selectedGame?.id) || refreshedGames[0]
      if (refreshedSelected) selectGame(refreshedSelected)
      setSyncMessage(`${achievementsResult.achievementsSynced} conquistas sincronizadas.`)
    } catch (reason) {
      setError(reason.message)
      setSyncMessage('')
    } finally {
      setSyncing(false)
    }
  }

  const filteredGames = useMemo(() => games.filter((game) => game.name.toLowerCase().includes(query.toLowerCase())), [games, query])
  const unlocked = achievements.filter((achievement) => achievement.achieved).length
  const progress = achievements.length ? Math.round((unlocked / achievements.length) * 100) : 0

  if (showLogin) return <LoginScreen onContinue={() => setShowLogin(false)} />

  return <main className="app-shell">
    <header className="topbar"><div className="brand"><span className="brand-mark">GP</span><span>GAMER PROFILE</span></div><div className="topbar-actions"><div className="status"><span className="pulse" /> STEAM CONNECTED</div><button className="sync-button" onClick={synchronizeSteam} disabled={syncing}>{syncing ? 'SINCRONIZANDO...' : 'SINCRONIZAR STEAM'}</button></div></header>
    <section className="hero"><div><p className="eyebrow">UNIFIED GAMING IDENTITY / 001</p><h1>Your library.<br /><em>Your progress.</em></h1><p className="hero-copy">Uma visão única de tudo que você joga, conquista e desbloqueia.</p></div><div className="hero-stat"><strong>{summary?.totalGames || games.length || '—'}</strong><span>JOGOS<br />SINCRONIZADOS</span></div></section>
    <section className="summary-grid"><div><strong>{summary?.totalGames ?? '—'}</strong><span>JOGOS TOTAIS</span></div><div><strong>{summary ? Math.round(summary.totalPlaytimeMinutes / 60) : '—'}</strong><span>HORAS JOGADAS</span></div><div><strong>{summary?.unlockedAchievements ?? '—'}</strong><span>CONQUISTAS</span></div><div><strong>{summary ? `${summary.completionPercentage}%` : '—'}</strong><span>CONCLUSÃO GERAL</span></div></section>
    {error && <div className="notice">{error} <span>Verifique se o backend está rodando em localhost:8080.</span></div>}
    {syncMessage && <div className="sync-message">{syncMessage}</div>}
    <section className="content-grid">
      <aside className="library-panel"><div className="panel-heading"><div><p className="eyebrow">COLLECTION</p><h2>Biblioteca</h2></div><span className="count">{games.length} JOGOS</span></div><label className="search"><span>⌕</span><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar jogo..." /></label><div className="game-list game-grid">{loading && <p className="muted">Carregando biblioteca...</p>}{!loading && filteredGames.map((game) => <button className={`game-card ${selectedGame?.id === game.id ? 'active' : ''}`} key={game.id} onClick={() => selectGame(game)}><div className="cover-wrap">{game.externalId ? <img src={`https://cdn.cloudflare.steamstatic.com/steam/apps/${game.externalId}/library_600x900_2x.jpg`} onError={(event) => { if (game.imageUrl && event.currentTarget.src !== game.imageUrl) event.currentTarget.src = game.imageUrl }} alt={`Capa de ${game.name}`} /> : <span className="cover-fallback">{game.name.slice(0, 1)}</span>}<span className="card-platform">STEAM</span></div><span className="card-title">{game.name}</span><small>{Math.round((game.playtimeMinutes || 0) / 60)}h jogadas</small></button>)}</div></aside>
      <section className="detail-panel">{selectedGame ? <><div className="detail-heading"><div><p className="eyebrow">SELECTED GAME / {selectedGame.externalId}</p><h2>{selectedGame.name}</h2></div><span className="platform-badge">STEAM</span></div><div className="metrics"><div><strong>{selectedGame.playtimeMinutes || 0}</strong><span>MINUTOS JOGADOS</span></div><div><strong>{achievements.length || '—'}</strong><span>CONQUISTAS</span></div><div><strong>{progress}%</strong><span>CONCLUSÃO</span></div></div><div className="achievement-header"><div><p className="eyebrow">ACHIEVEMENTS</p><h3>Conquistas</h3></div><span>{unlocked} / {achievements.length}</span></div>{loadingAchievements ? <p className="muted">Carregando conquistas...</p> : <div className="achievement-grid">{achievements.map((achievement) => <article className={`achievement ${achievement.achieved ? 'unlocked' : ''}`} key={achievement.id}><div className="achievement-icon">{achievement.iconUrl ? <img src={achievement.iconUrl} alt="" /> : '✦'}</div><div><h4>{achievement.name}</h4><p>{achievement.description || 'Conquista da sua jornada.'}</p><span>{achievement.achieved ? 'DESBLOQUEADA' : 'BLOQUEADA'}</span></div></article>)}</div>}</> : <div className="empty"><span>✦</span><h2>Biblioteca vazia</h2><p>Sincronize sua conta Steam para começar.</p></div>}</section>
    </section><footer><span>GAMER PROFILE / PRIVATE BUILD</span><span>JAVA 21 · SPRING BOOT · REACT</span></footer>
  </main>
}

function LoginScreen({ onContinue }) {
  return <main className="login-shell">
    <div className="login-grid" />
    <header className="login-topbar"><div className="brand"><span className="brand-mark">GP</span><span>GAMER PROFILE</span></div><span className="login-version">PRIVATE BUILD / 001</span></header>
    <section className="login-hero">
      <p className="eyebrow">YOUR GAMING IDENTITY, UNIFIED</p>
      <h1>Todos os seus<br /><em>jogos. Em um só lugar.</em></h1>
      <p className="login-copy">Conecte suas plataformas, acompanhe suas conquistas e transforme cada partida em progresso.</p>
      <div className="login-actions">
        <a className="steam-login" href="http://localhost:8080/auth/steam"><span>◈</span> ENTRAR COM STEAM <b>↗</b></a>
        <button className="continue-button" onClick={onContinue}>CONTINUAR SEM CONECTAR</button>
      </div>
      <p className="login-note">Você será redirecionado para a Steam para autorizar o acesso. Nós nunca vemos sua senha.</p>
    </section>
    <section className="platform-preview"><span>PLATAFORMAS</span><strong>STEAM</strong><i>RETROACHIEVEMENTS</i><i>XBOX</i><i>PLAYSTATION</i></section>
    <footer><span>GAMER PROFILE / PRIVATE BUILD</span><span>JAVA 21 · SPRING BOOT · REACT</span></footer>
  </main>
}

export default App
