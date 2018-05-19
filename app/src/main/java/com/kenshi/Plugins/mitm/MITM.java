package com.kenshi.Plugins.mitm;

import com.kenshi.Core.Plugin;
import com.kenshi.Network.NetworkManager.Target;
import com.mitdroid.kenshi.Main.R;

public class MITM extends Plugin {
    public MITM() {
        super(
                "MITM",
                "Perform various man-in-the-middle attacks, such as network sniffing," +
                        " traffic manipulation, etc.",
                new Target.Type[]{ Target.Type.ENDPOINT, Target.Type.NETWORK },
                R.layout.plugin_mitm,
                R.drawable.action_mitm
        );
    }
}
