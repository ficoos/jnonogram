jn.availableGamesListeners = [];
let availableGamesListUpdater = function() {
    $.ajax({
        url: '/api/games',
        dataType: 'json',
        method: 'get',
        cache: false,
        success: function (resp) {
            let games = resp.games;
            for(let listener of jn.availableGamesListeners) {
                listener(games);
            }
        },
        error: function (e) {
        }.bind(this)
    })
};

jn.connectedPlayersListener = [];
let connectedPlayers = function() {
    $.ajax({
        url: '/api/players',
        dataType: 'json',
        method: 'get',
        cache: false,
        success: function (resp) {
            let players = resp.players;
            for(let listener of jn.connectedPlayersListener) {
                listener(players);
            }
        },
        error: function (e) {
        }.bind(this)
    })
};

window.setInterval(jn.userInformationUpdater, 2000);
window.setInterval(availableGamesListUpdater, 2000);
window.setInterval(connectedPlayers, 2000);

jn.WelcomHeader = React.createClass({
    _updateCallback: null,

    getInitialState() {
        return {username: ''}
    },
    componentDidMount() {
        jn.userInformationUpdater();
        this._updateCallback = (userInfo) => {
            this.setState({username: userInfo.username})
        };
        jn.userInfoUpdateListeners.push(this._updateCallback)
    },
    componentWillUnmount() {
        if (this._updateCallback) {
            jn.userInfoUpdateListeners.remove(this._updateCallback)
        }
    },
    render() {
        return (
            <div className="page-header">
                <h1>Hello {this.state.username}, welcome to the lobby <small>Stay a while</small></h1>
            </div>
        );
    }
});

jn.GameListTable = React.createClass({
    _updateCallback: null,

    getInitialState() {
        return {games: []}
    },
    componentDidMount() {
        availableGamesListUpdater();
        this._updateCallback = (games) => {
            this.setState({games: games})
        };
        jn.availableGamesListeners.push(this._updateCallback)
    },
    onJoinClicked(e, id) {
        $.ajax({
            url: '/api/games',
            data: {id: id, action: 'join'},
            method: 'get',
            cache: false,
            dataType: 'json',
            success: function (resp) {
                window.location.replace("/game.html?id=" + id);
            },
            error: function (e) {
                console.log(e);
                alert("Could not join game: " + e.responseJSON.errors[0].message);
            }.bind(this)
        })

    },
    render() {
        return (
            <div>
                <h2>Available Games</h2>
                <jn.GameUploader />
                <hr />
                <table className="table table-striped table-hover">
                    <thead>
                    <th>Title</th><th>Creator</th><th>Players Status</th><th>Max Moves</th><th>Board Size</th><th>Actions</th>
                    </thead>
                    <tbody>
                    {
                        this.state.games.map((game) =>
                            <tr>
                                <td>{game.title}</td>
                                <td>{game.creator}</td>
                                <td>{game.player_count} out of {game.max_players}</td>
                                <td>{game.max_moves}</td>
                                <td>{game.board_height}X{game.board_width}</td>
                                <td><button className="btn btn-xs btn-default" onClick={(e) => this.onJoinClicked(e, game.id)}>Join</button></td>
                            </tr>
                        )
                    }
                    </tbody>
                </table>
            </div>
        );
    },
    componentWillUnmount() {
        if (this._updateCallback) {
            jn.availableGamesListeners.remove(this._updateCallback)
        }
    }
});

jn.GameUploader = React.createClass({
    getInitialState() {
        return {errors: [], uploadSuccessful: false};
    },
    resetState() {
        this.setState({uploadSuccessful: false, errors: []});
    },
    onFormSubmit(e) {
        e.preventDefault();
        this.resetState();
        let data = new FormData();
        let form = e.target;
        let fileElement = $(e.target).find('input')[0];
        data.append('file', fileElement.files[0]);
        $.ajax({
            url: '/api/games',
            contentType: false,
            processData: false,
            dataType: 'json',
            method: 'post',
            data: data,
            success: function (resp) {
                form.reset();
                this.setState({uploadSuccessful: true})
            }.bind(this),
            error: function (e) {
                this.setState({errors: e.responseJSON.errors});
            }.bind(this)
        });
    },
    render() {
        return (
            <div>
                {
                    this.state.uploadSuccessful ? <jn.Message kind="success" message="Game created successfully" /> : null
                }
                <jn.ErrorBox errors={this.state.errors} />
                <form method="post" onSubmit={this.onFormSubmit}>
                    <div className="form-group">
                        <label htmlFor="exampleInputFile">Game File</label>
                        <input type="file" id="exampleInputFile" />
                        <p className="help-block">Upload game XML files.</p>
                    </div>
                    <button className="btn btn-primary" value="createGame" type="submit">Create Game</button>
                </form>
            </div>
        );
    }
});

jn.PlayerListTable = React.createClass({
    _updateCallback: null,

    getInitialState() {
        return {players: []}
    },
    componentDidMount() {
        connectedPlayers();
        this._updateCallback = (players) => {
            this.setState({players: players})
        };
        jn.connectedPlayersListener.push(this._updateCallback)
    },
    componentWillUnmount() {
        if (this._updateCallback) {
            jn.availableGamesListeners.remove(this._updateCallback)
        }
    },
    render() {
        return (
            <div>
                <h2>Connected Players</h2>
                <table className="table table-striped table-hover">
                    <thead>
                    <th>Player Name</th>
                    </thead>
                    <tbody>
                    {
                        this.state.players.map((player) =>
                            <tr>
                                <td>{player.username}</td>
                            </tr>
                        )
                    }
                    </tbody>
                </table>
            </div>
        );
    }
});
