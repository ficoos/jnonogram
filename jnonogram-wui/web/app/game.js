jn.gameInformationListener = [];
let gameInformationUpdater = function() {
    let id = jn.getQueryString('id', window.location.href);
    $.ajax({
        url: '/api/games',
        data: {id: id},
        dataType: 'json',
        method: 'get',
        cache: false,
        success: function (resp) {
            let game = resp.games[0];
            for(let listener of jn.gameInformationListener) {
                listener(game);
            }
        },
        error: function (e) {
        }.bind(this)
    })
};

window.setInterval(jn.userInformationUpdater, 2000);

let range = function (n) {
    let res = [];
    for (let i = 0; i < n; i++) {
        res[i] = i;
    }

    return res;
};

jn.NonogramView = React.createClass({
    getInitialState() {
        return {nonogram: {
            row_count: 0,
            column_count: 0,
            cells: []
        }};
    },
    _updateNonogramCallback(game) {
        this.setState({nonogram: game.nonogram});
    },
    componentDidMount() {
        gameInformationUpdater();
        jn.gameInformationListener.push(this._updateNonogramCallback);
    },
    componentWillUnmount() {
        jn.gameInformationListener.remove(this._updateNonogramCallback);
    },
    onCellClicked(e, row, column) {
        e.preventDefault();
        let nonogram = this.state.nonogram;
        let cellIndex = row * nonogram.column_count + column;
        let which = e.nativeEvent.which;
        if (which == 1) {
            if (nonogram.cells[cellIndex] == "Black") {
                nonogram.cells[cellIndex] = "White";
            } else {
                nonogram.cells[cellIndex] = "Black";
            }
        } else if (which == 2) {
            nonogram.cells[cellIndex] = "Unknown";
        }

        this.setState({nonogram: nonogram});
    },
    render() {
        let self = this;
        return (
            <div>
                <table>
                    {
                        range(self.state.nonogram.row_count).map(function(i) {
                            return (
                                <tr>
                                    {
                                        range(self.state.nonogram.column_count).map(function (j) {
                                            return (
                                                <td
                                                    className={"ncell ncell-" + self.state.nonogram.cells[i * self.state.nonogram.column_count + j].toLowerCase()}
                                                    onClick={(e) => self.onCellClicked(e, i, j)}
                                                >
                                                </td>
                                            );
                                        })
                                    }
                                </tr>
                            );
                        })
                    }
                </table>
            </div>
        );
   }
});
