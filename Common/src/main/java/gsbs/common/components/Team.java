package gsbs.common.components;

import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;

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

	public boolean isInSameTeam(Entity other) {
		Team team2 = other.getComponent(Team.class);
		if (team == null || team2 == null) {
			return false;
		}
		return this.getTeam().equals(team2.getTeam());
	}
}
