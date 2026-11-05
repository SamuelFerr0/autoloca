import { useEffect, useRef } from "react";
import { X } from "lucide-react";
export function Message({ erro, sucesso }) {
  if (erro) {
    return (
      <div role="alert" className="message error">
        {erro}
      </div>
    );
  }
  if (sucesso) {
    return (
      <div role="status" className="message success">
        {sucesso}
      </div>
    );
  }
  return null;
}

export function Modal({ titulo, children, onClose }) {
  const ref = useRef(null);
  useEffect(() => {
    const dialog = ref.current;
    dialog.showModal();
    return () => dialog.close();
  }, []);
  return (
    <dialog
      ref={ref}
      onCancel={(e) => {
        e.preventDefault();
        onClose();
      }}
      aria-labelledby="modal-title"
    >
      <div className="modal-head">
        <h2 id="modal-title">{titulo}</h2>
        <button className="icon-button" aria-label="Fechar" onClick={onClose}>
          <X size={20} />
        </button>
      </div>
      {children}
    </dialog>
  );
}
export function Campo({ label, ...props }) {
  return (
    <label className="field">
      {label}
      <input {...props} />
    </label>
  );
}
export function Submit({ busy, children = "Salvar" }) {
  return (
    <button className="button" disabled={busy} type="submit">
      {busy ? "Aguarde…" : children}
    </button>
  );
}
export function Empty({ children }) {
  return (
    <div className="empty">
      <p>{children}</p>
    </div>
  );
}
