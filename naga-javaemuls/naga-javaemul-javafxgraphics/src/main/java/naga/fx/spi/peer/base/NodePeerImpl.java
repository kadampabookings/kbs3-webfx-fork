package naga.fx.spi.peer.base;

import emul.javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public abstract class NodePeerImpl
        <N extends Node, NB extends NodePeerBase<N, NB, NM>, NM extends NodePeerMixin<N, NB, NM>>
        implements NodePeerMixin<N, NB, NM> {

    private final NB base;

    public NodePeerImpl(NB base) {
        this.base = base;
        base.setMixin((NM) this);
    }

    @Override
    public NB getNodePeerBase() {
        return base;
    }

}
