let checkLoggedIn = function() {
    $.ajax({
        url: '/api/me',
        dataType: 'json',
        method: 'get',
        cache: false,
        success(resp) {
            window.location.replace("/lobby.html");
        }
    });
};

checkLoggedIn();

jn.LoginForm = React.createClass({
    getInitialState() {
        return {username: '', errors: []}
    },
    handleUsernameChange(e) {
        this.setState({username: e.target.value});
    },
    handleFormSubmission(e) {
        e.preventDefault();
        let username = this.state.username.trim();
        $.ajax({
            url: '/api/login',
            dataType: 'json',
            method: 'post',
            cache: false,
            data: JSON.stringify({username: username}),
            success: function (resp) {
                window.location.replace("/lobby.html");
            },
            error: function (e) {
                this.setState({errors: e.responseJSON.errors});
            }.bind(this)
        })
    },
    render() {
        return (
            <div>
                <jn.ErrorBox errors={this.state.errors}/>
                <div className="col-md-4">
                    <form method="post" onSubmit={this.handleFormSubmission}>
                        <div className="form-group">
                            <label htmlFor="username">User Name:</label>
                            <input type="text" className="form-control" id="username" name="username"
                                   placeholder="User name"
                                   value={this.state.username}
                                   onChange={this.handleUsernameChange}
                            />
                        </div>
                        <button type="submit" value="login" className="btn btn-primary">Log in</button>
                    </form>
                </div>
            </div>
        );
    }
})
