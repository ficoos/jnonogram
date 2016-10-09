// Initialize the namespace
window.jn = {};

jn.userInfo = {};
jn.userInfoUpdateListeners = [];
jn.userInformationUpdater = function() {
    $.ajax({
        url: '/api/me',
        dataType: 'json',
        method: 'get',
        cache: false,
        success: function (resp) {
            jn.userInfo = resp.players[0];
            jn.userInfo = resp.players[0];
            for(let listener of jn.userInfoUpdateListeners) {
                listener(jn.userInfo);
            }
        },
        error: function (e) {
            // We are not logged in
            window.location.replace("/");
        }.bind(this)
    })
};

jn.ErrorBox = React.createClass({
    render() {
        return (
            <div>
                {
                    this.props.errors.map((error)=>
                        <jn.Message kind="danger" message={error.message} />
                    )
                }
            </div>
        )
    }
});

jn.Message = React.createClass({
    render() {
        return (
            <div className={"alert alert-" + this.props.kind}>{this.props.message}</div>
        );
    }
});

jn.LogOutButton = React.createClass({
    onButtonClicked(e) {
        $.ajax({
            url: '/api/logout',
            dataType: 'json',
            method: 'post',
            cache: false,
            success: function (resp) {
                window.location.replace("/");
            }
        });
    },
    render() {
        return (
            <button className="btn btn-danger" onClick={this.onButtonClicked}>Log Out</button>
        );
    }
});

jn.getQueryString = function (field, url) {
    var href = url ? url : window.location.href;
    var reg = new RegExp( '[?&]' + field + '=([^&#]*)', 'i' );
    var string = reg.exec(href);
    return string ? string[1] : null;
};

