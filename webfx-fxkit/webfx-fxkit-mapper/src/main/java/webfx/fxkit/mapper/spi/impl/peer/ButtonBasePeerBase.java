package webfx.fxkit.mapper.spi.impl.peer;

import javafx.scene.control.ButtonBase;

/**
 * @author Bruno Salmon
 */
public class ButtonBasePeerBase
        <N extends ButtonBase, NB extends ButtonBasePeerBase<N, NB, NM>, NM extends ButtonBasePeerMixin<N, NB, NM>>

        extends LabeledPeerBase<N, NB, NM> {
}