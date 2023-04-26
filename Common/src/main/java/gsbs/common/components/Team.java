package gsbs.common.components;

import gsbs.common.data.enums.Teams;

public class Team extends Component{
	private Teams team;

	public Team(Teams team) {
		this.team = team;
	}

	public Teams getTeam() {
		return team;
	}

	public void setTeam(Teams team) {
		this.team = team;
	}
}
